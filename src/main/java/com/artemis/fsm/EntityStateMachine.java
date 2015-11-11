package com.artemis.fsm;

import com.artemis.Component;
import com.artemis.Entity;
import com.badlogic.gdx.utils.*;

import java.util.Iterator;

/**
 /**
 * This is a state machine for an entity. The state machine manages a set of states,
 * each of which has a set of component providers. When the state machine changes the state, it removes
 * components associated with the previous state and adds components associated with the new state.
 *
 * {@link EntityStateMachine EntityStateMachine} is accessed through
 * {@link com.artemis.Entity#getEntityStateMachine getEntityStateMachine}.
 * {@link EntityState EntityStates} and
 * {@link ComponentProvider ComponentProviders} should be created with this class.
 * ComponentProviders are linked to EntityStates with
 * {@link EntityState#add(ComponentProvider)} add}.
 *
 * Activate a state with {@link com.artemis.Entity#activateFiniteState(Object) activateFiniteState}
 *
 * Inspired by article by an article by Richard Lord: http://www.richardlord.net/blog/finite-state-machines-with-ash
 *
 * @author  Vemund Kvam on 10/06/14.
 */

public class EntityStateMachine implements Pool.Poolable {
    private Entity entity;
    private EntityState currentState;
    private ObjectMap<Object,EntityState> finiteStateForIdentifier = new ObjectMap<Object, EntityState>(4);;

    private int nextProviderComponentClassId = 0;
    private ObjectIntMap<Class<? extends Component>> classIndexForComponent = new ObjectIntMap<Class<? extends Component>>(4);

    private int nextProviderInstanceId = 0;
    private IntMap<ComponentProvider> providerForIndex = new IntMap<ComponentProvider>(4);;
    private boolean resetting = false;

    /**
     * Should not be accessed by user, the entity is set when the EntityStateMachine is created by the entity.
     * @param entity
     */
    public void setEntity(Entity entity){
        this.entity = entity;
    }

    /**
     * Retrieves a new {@link ComponentProvider ComponentProvider}, either by creating a
     * new one or by obtaining one from the pool.
     *
     * @param type the class of the componentProvider to be created
     * @param <C> the type of the componentProvider to be created
     * @return a pooled ComponentProvider
     */
    public <C extends ComponentProvider> C createComponentProvider(Class<C> type) {
        C componentProvider = Pools.obtain(type);
        componentProvider.setEntity(entity);
        return componentProvider;
    }

    /**
     *
     * Removes components that were provided by the current state.
     */
    public void deactivateCurrentState(){
        Bits providerBits = currentState.providerIndicesBits;
        for (int i = providerBits.nextSetBit(0); i >= 0; i = providerBits.nextSetBit(i+1)) {
            ComponentProvider provider = providerForIndex.get(i);
            provider.removedFromEntity();
            entity.removeComponent(provider.lastComponentProduced);
        }
        currentState =null;
    }

    /**
     * Removes components that were provided by the current state and adds components provided by new state.
     * Components remains untouched if the componentProvider on both states are the same.
     *
     * @param id the id of the new state.
     */
    public void activateState(Object id){
        EntityState newState = finiteStateForIdentifier.get(id);

        if(currentState != null) {

            if(newState.equals(currentState)){
                return;
            }

            ComponentProvider provider;

            Bits providerRemoveBits = currentState.getProviderIndicesCopy(newState);
            Bits providerAddBits = newState.getProviderIndicesCopy(currentState);

            for (int i = providerAddBits.nextSetBit(0); i >= 0; i = providerAddBits.nextSetBit(i + 1)) {
                provider = providerForIndex.get(i);
                int lastProviderInstanceIndex = currentState.getProviderIndex(provider.classIndex);
                if(lastProviderInstanceIndex!=-1){
                    providerForIndex.get(lastProviderInstanceIndex).removedFromEntity();
                    providerRemoveBits.clear(lastProviderInstanceIndex);
                }
                entity.addComponent(provider.createComponent());
            }

            for (int i = providerRemoveBits.nextSetBit(0); i >= 0; i = providerRemoveBits.nextSetBit(i+1)) {
                provider = providerForIndex.get(i);
                provider.removedFromEntity();
                entity.removeComponent(provider.lastComponentProduced);
            }

        }else{
            Bits newStateBits = newState.providerIndicesBits;
            for (int i = newStateBits.nextSetBit(0); i >= 0; i = newStateBits.nextSetBit(i + 1)) {
                entity.addComponent(providerForIndex.get(i).createComponent());
            }
        }
        currentState = newState;
    }

    /**
     * Creates a new {@link EntityState EntityState}
     *
     * @param id the object to use as identifier to the state.
     * @return the new state
     */
    public EntityState createState(Object id){
        EntityState entityState = Pools.obtain(EntityState.class);
        entityState.setEntityStateMachine(this);
        finiteStateForIdentifier.put(id, entityState);
        return entityState;
    }

    /**
     * Deletes a state.
     *
     * Removes {@link ComponentProvider componentProviders} when the deleted state is
     * the last to reference them. If the deleted state is the current active state,
     * the active state is deactivated and its provided components are removed.
     *
     * @param id the id of the state to delete
     */
    public void deleteState(Object id){
        EntityState entityState = finiteStateForIdentifier.remove(id);
        if (entityState.equals(currentState)) {
            deactivateCurrentState();
        }
        Pools.free(entityState);
        finiteStateForIdentifier.remove(id);
    }

    /**
     * Used by {@link EntityState EntityState} to remove ComponentProviders
    */
    protected void removeComponentProvider(int providerInstanceIndex){
        if(!resetting){
            boolean noneHaveProvider = true;
            for (EntityState entityState : finiteStateForIdentifier.values()) {
                if (entityState.providerIndicesBits.get(providerInstanceIndex)) {
                    noneHaveProvider = false;
                    break;
                }
            }
            if (noneHaveProvider) {
                Pools.free(providerForIndex.remove(providerInstanceIndex));
            }
        }
    }

    /**
     * Returns the index of a ComponentProvider instance. Indices are cached, so retrieval
     * should be fast.
     *
     * @param provider ComponentProvider instance to retrieve the index for.
     * @return Index of a specific component instance.
     */
    protected int getProviderIndex(ComponentProvider provider) {
        Integer index = providerForIndex.findKey(provider,false,-1);
        if (index==-1) {
            index = nextProviderInstanceId++;
            providerForIndex.put(index, provider);
        }
        return index;
    }

    /**
     * Returns the index of a Component class. Indices are cached, so retrieval
     * should be fast.
     *
     * @param provider Provider to retrieve the component class index for.
     * @return Index of a specific component class.
     */
    protected int getProviderComponentClassIndex(ComponentProvider provider) {
        int index = classIndexForComponent.get(provider.componentClass, -1);
        if (index == -1) {
            index = nextProviderComponentClassId++;
            classIndexForComponent.put(provider.componentClass, index);
        }
        return index;
    }

    @Override
    public void reset() {
        resetting = true;

        Iterator<EntityState> stateIterator = finiteStateForIdentifier.values().iterator();
        while(stateIterator.hasNext()){
            Pools.free(stateIterator.next());
            stateIterator.remove();
        }

        Iterator<ComponentProvider> providerIterator = providerForIndex.values().iterator();
        while(providerIterator.hasNext()){
            Pools.free(providerIterator.next());
            providerIterator.remove();
        }

        classIndexForComponent.clear();
        nextProviderComponentClassId = 0;
        nextProviderInstanceId = 0;
        currentState=null;
        resetting = false;
        entity=null;
    }

}

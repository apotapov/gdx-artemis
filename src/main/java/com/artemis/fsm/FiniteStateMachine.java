package com.artemis.fsm;

import com.artemis.Component;
import com.artemis.Entity;
import com.badlogic.gdx.utils.*;

import java.util.Iterator;

/**
 * Provides a convenient way of creating and adding groups of components to an entity.
 * Accessed through {@link com.artemis.Entity#getFiniteStateMachine Entity}, abstract to
 * encourage this.
 *
 * Based on article http://www.richardlord.net/blog/finite-state-machines-with-
 * Created by Vemund Kvam on 10/06/14.
 */
public class FiniteStateMachine implements Pool.Poolable {
    private Entity entity;
    private FiniteState currentState;
    private ObjectMap<Object,FiniteState> finiteStateForIdentifier = new ObjectMap<Object, FiniteState>(4);;

    private int nextProviderComponentClassId = 0;
    private ObjectIntMap<Class<? extends Component>> classIndexForComponent = new ObjectIntMap<Class<? extends Component>>(4);

    private int nextProviderInstanceId = 0;
    private IntMap<ComponentProvider> providerForIndex = new IntMap<ComponentProvider>(4);;
    private boolean resetting = false;

    /**
     * Should not be accessed by user, the entity is set by the entity when creating the FiniteStateMachine.
     * @param entity
     */
    public void setEntity(Entity entity){
        this.entity = entity;
    }

    /**
     * Retrieves a new {@link com.artemis.fsm.ComponentProvider ComponentProvider}, either by creating a
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
     * Removes components that were provided by the current state.
     */
    public void deactivateCurrentState(){
        Bits providerBits = currentState.providerIndicesBits;
        for (int i = providerBits.nextSetBit(0); i >= 0; i = providerBits.nextSetBit(i+1)) {
            ComponentProvider provider = providerForIndex.get(i);
            provider.removedFromEntity();
            entity.removeComponent(provider.getLastComponent());
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
        FiniteState newState = finiteStateForIdentifier.get(id);

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
                entity.removeComponent(provider.getLastComponent());
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
     * Creates a new {@link com.artemis.fsm.FiniteState FiniteState}
     *
     * @param id the object to use as identifier to the state.
     * @return the new state
     */
    public FiniteState createState(Object id){
        FiniteState finiteState = Pools.obtain(FiniteState.class);
        finiteState.setFiniteStateMachine(this);
        finiteStateForIdentifier.put(id, finiteState);
        return finiteState;
    }

    /**
     * Deletes a state.
     *
     * Removes {@link com.artemis.fsm.ComponentProvider componentProviders} if the deleted
     * state is the last to reference them. If the deleted state is the current active state,
     * the active state is deactivated and its provided components are removed.
     *
     * @param id the id of the state to delete
     */
    public void deleteState(Object id){
        FiniteState finiteState = finiteStateForIdentifier.remove(id);
        if (finiteState.equals(currentState)) {
            deactivateCurrentState();
        }
        Pools.free(finiteState);
        finiteStateForIdentifier.remove(id);
    }

    /**
     * Used by {@link com.artemis.fsm.FiniteState FiniteState} to remove ComponentProviders
     *
    */
    protected void removeComponentProvider(int providerInstanceIndex){
        if(!resetting){
            boolean noneHaveProvider = true;
            for (FiniteState finiteState : finiteStateForIdentifier.values()) {
                if (finiteState.providerIndicesBits.get(providerInstanceIndex)) {
                    noneHaveProvider = false;
                    break;
                }
            }
            if (noneHaveProvider) {
                Pools.free(providerForIndex.remove(providerInstanceIndex));
            }
        }
    }

    @Override
    public void reset() {
        resetting = true;

        Iterator<FiniteState> stateIterator = finiteStateForIdentifier.values().iterator();
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
        int index = classIndexForComponent.get(provider.getComponentClass(), -1);
        if (index == -1) {
            index = nextProviderComponentClassId++;
            classIndexForComponent.put(provider.getComponentClass(), index);
        }
        return index;
    }

}

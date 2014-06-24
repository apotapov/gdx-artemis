package com.artemis.fsm;

import com.artemis.Component;
import com.artemis.Entity;
import com.badlogic.gdx.utils.*;

import java.util.Iterator;

/**
 * Created by Vemund Kvam on 10/06/14.
 *
 */
public class FiniteStateMachine implements Pool.Poolable {
    private Entity entity;

    private FiniteState currentState;
    private ObjectMap<Object,FiniteState> finiteStateForIdentifier;

    private int nextProviderComponentClassId = 0;
    private int nextProviderInstanceId = 0;
    private ObjectIntMap<Class<? extends Component>> classIndexForComponent;
    private IntMap<ComponentProvider> providerForIndex;
    private boolean resetting = false;

    public FiniteStateMachine(){
        finiteStateForIdentifier = new ObjectMap<Object, FiniteState>(4);
        providerForIndex = new IntMap<ComponentProvider>(4);
        classIndexForComponent = new ObjectIntMap<Class<? extends Component>>(4);
    }

    public void setEntity(Entity entity){
        this.entity = entity;
    }

    public <C extends ComponentProvider> C createComponentProvider(Class<C> type) {
        C componentProvider = Pools.obtain(type);
        componentProvider.setEntity(entity);
        return componentProvider;
    }

    /**
     * Removes all components that were produced by the current state.
     *
     *
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
     * Removes components provided by current state and adds components provided by new state.
     *
     * Components remains untouched if a componentProvider is present on both new and current state.
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

            Bits providerAddBits = newState.getBitsCopy(currentState);
            Bits providerRemoveBits = currentState.getBitsCopy(newState);

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
     * Creates a new state
     *
     * @param id the id of the state.
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
     * Removes componentProviders if the state is the last to reference them.
     * If the deleted state is the current active state, the active state is deactivated.
     *
     * @param id the id of the state to delete
     * @return if the state was deleted successfully.
     */
    public boolean deleteState(Object id){
            FiniteState finiteState = finiteStateForIdentifier.remove(id);
            if (finiteState.equals(currentState)) {
                deactivateCurrentState();
            }
            Pools.free(finiteState);
            finiteStateForIdentifier.remove(id);
            return true;
    }

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
                Pools.free(providerForIndex.get(providerInstanceIndex));
            }
        }
    }

    @Override
    public void reset() {
        resetting = true;

        Iterator<FiniteState> stateIterator = finiteStateForIdentifier.values().iterator();
        while(stateIterator.hasNext()){
            Pools.free(stateIterator.next());
        }
        Iterator<ComponentProvider> providerIterator = providerForIndex.values().iterator();
        while(providerIterator.hasNext()){
            Pools.free(providerIterator.next());
        }

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
            index= nextProviderComponentClassId++;
            classIndexForComponent.put(provider.getComponentClass(), index);
        }
        return index;
    }

}

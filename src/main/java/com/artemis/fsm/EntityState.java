package com.artemis.fsm;
import com.badlogic.gdx.utils.*;

/**
 *
 *
 * @author Vemund Kvam on 10/06/14.
 */
public class EntityState implements Pool.Poolable{
    private EntityStateMachine entityStateMachine;
    private ObjectIntMap<Integer> providerIndexForProviderComponentClassIndex = new ObjectIntMap<Integer>(4);
    private int bitSize=0;
    protected Bits providerIndicesBits = new Bits();;
    protected Bits bitsCopy = new Bits();;

    protected EntityState(){;
    }

    /**
     * Adds a {@link ComponentProvider ComponentProvider} to this state. Components created
     * by providers will be added once the state is activated wit
     * {@link com.artemis.Entity#activateFiniteState(Object)}  entity.activateFiniteState}.
     *
     * @param componentProvider to add to this state.
     * @return this EntityState for chaining.
     */
    public EntityState add(ComponentProvider componentProvider){
        if(!componentProvider.addedStateMachine) {
            componentProvider.onProviderInit();
            int providerInstanceIndex = entityStateMachine.getProviderIndex(componentProvider);
            int providerComponentClassIndex = entityStateMachine.getProviderComponentClassIndex(componentProvider);
            componentProvider.setIndices(providerComponentClassIndex,providerInstanceIndex);
            providerIndicesBits.set(providerInstanceIndex);
            ensureBitCopySize(providerInstanceIndex);
            providerIndexForProviderComponentClassIndex.put(providerComponentClassIndex, providerInstanceIndex);
        }else {
            providerIndicesBits.set(componentProvider.instanceIndex);
            providerIndexForProviderComponentClassIndex.put(componentProvider.classIndex, componentProvider.instanceIndex);
        }

        return this;
    }

    /**
     * Removes a {@link ComponentProvider ComponentProvider} from this state.
     *
     * When the componentProvider is removed from all states on the parent
     * {@link EntityStateMachine EntityStateMachine}, the
     * provider is pooled.
     *
     * @param componentProvider to remove from this state.
     */
    public void remove(ComponentProvider componentProvider){
        int providerIndex = componentProvider.instanceIndex;
        remove(providerIndex);
    }

    private void remove(int providerIndex){
        providerIndicesBits.clear(providerIndex);
        entityStateMachine.removeComponentProvider(providerIndex);
        providerIndexForProviderComponentClassIndex.remove(providerIndex, -1);
    }

    protected int getProviderIndex(int componentClassIndex){
        return providerIndexForProviderComponentClassIndex.get(componentClassIndex,-1);
    }

    /**
     * @param excludeState providers from this state will be excluded
     * @return Bits representing the providers on this state.
     */
    protected Bits getProviderIndicesCopy(EntityState excludeState){
        bitsCopy.clear();
        bitsCopy.or(providerIndicesBits);
        bitsCopy.andNot(excludeState.providerIndicesBits);
        return bitsCopy;
    }

    private void ensureBitCopySize(int index){
        if(bitSize <index){
            bitsCopy = new Bits(index);
            bitSize = bitsCopy.numBits();
        }
    }

    protected void setEntityStateMachine(EntityStateMachine entityStateMachine) {
        this.entityStateMachine = entityStateMachine;
    }

    @Override
    public void reset() {
        // Remove all componentProviders
        for (int i = providerIndicesBits.nextSetBit(0); i >= 0; i = providerIndicesBits.nextSetBit(i + 1)) {
            remove(i);
        }
        providerIndicesBits.clear();
        providerIndexForProviderComponentClassIndex.clear();
    }
}

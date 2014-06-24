package com.artemis.fsm;
import com.badlogic.gdx.utils.*;

/**
 *
 *
 * @author Vemund Kvam on 10/06/14.
 */
public class FiniteState implements Pool.Poolable{
    private FiniteStateMachine finiteStateMachine;
    private ObjectIntMap<Integer> providerIndexForProviderComponentClassIndex = new ObjectIntMap<Integer>(4);
    private int bitSize=0;
    protected Bits providerIndicesBits = new Bits();;
    protected Bits bitsCopy = new Bits();;

    protected FiniteState(){;
    }

    public int getProviderIndex(int componentClassIndex){
        return providerIndexForProviderComponentClassIndex.get(componentClassIndex,-1);
    }

    /**
     * @param excludeState providers from this state will be excluded
     * @return Bits representing the providers on this state.
     */
    protected Bits getProviderIndicesCopy(FiniteState excludeState){
        bitsCopy.clear();
        bitsCopy.or(providerIndicesBits);
        bitsCopy.andNot(excludeState.providerIndicesBits);
        return bitsCopy;
    }

    /**
     * @param componentProvider to add to this state.
     * @return this FiniteState for chaining.
     */
    public FiniteState add(ComponentProvider componentProvider){
        if(!componentProvider.indicesSet) {
            int providerInstanceIndex = finiteStateMachine.getProviderIndex(componentProvider);
            int providerComponentClassIndex = finiteStateMachine.getProviderComponentClassIndex(componentProvider);
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

    private void ensureBitCopySize(int index){
        if(bitSize <index){
            bitsCopy = new Bits(index);
            bitSize = bitsCopy.numBits();
        }
    }

    /**
     * Removes a {@link com.artemis.fsm.ComponentProvider ComponentProvider} from this state.
     *
     * When the componentProvider is removed from all states in the parent
     * {@link com.artemis.fsm.FiniteStateMachine FiniteStateMachine}, the
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
        finiteStateMachine.removeComponentProvider(providerIndex);
        providerIndexForProviderComponentClassIndex.remove(providerIndex, -1);
    }

    protected void setFiniteStateMachine(FiniteStateMachine finiteStateMachine) {
        this.finiteStateMachine = finiteStateMachine;
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

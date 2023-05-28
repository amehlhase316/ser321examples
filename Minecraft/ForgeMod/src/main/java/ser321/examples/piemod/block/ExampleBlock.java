package ser321.examples.piemod.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.TntBlock;
import net.minecraft.world.level.block.state.BlockState;

public class ExampleBlock extends Block {

    public ExampleBlock( Properties properties ) {
        super(properties);
    }

    @Override
    public float getSpeedFactor() {
        return super.getSpeedFactor()*10f;
    }

    @Override
    public float getJumpFactor() {
        return super.getJumpFactor()*10f;
    }

    @Override
    public void fallOn( Level level, BlockState state, BlockPos pos, Entity entity, float distanceFalling ) {
        super.fallOn(level, state, pos, entity, 0);
    }

}

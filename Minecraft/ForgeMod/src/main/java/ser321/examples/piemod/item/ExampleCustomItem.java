package ser321.examples.piemod.item;

import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

public class ExampleCustomItem extends Item {

    public ExampleCustomItem( Properties properties ) {
        super(properties);
    }

    /**
     * This is called when the item is used, before the block is activated.
     *
     * @param stack
     * @param context
     * @return Return PASS to allow vanilla handling, any other to skip normal code.
     */
    @Override
    public InteractionResult onItemUseFirst( ItemStack stack, UseOnContext context ) {
        context
                .getLevel()
                .explode(
                        null,
                        context.getClickedPos().getX(),
                        context.getClickedPos().getY(),
                        context.getClickedPos().getZ(),
                        5.0f, Level.ExplosionInteraction.TNT
                );
        return InteractionResult.PASS;
    }

}

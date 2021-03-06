package com.finallion.graveyard_biomes.world.features.surfaceFeatures;

import com.finallion.graveyard_biomes.util.TGTags;
import com.mojang.serialization.Codec;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.DeadCoralWallFanBlock;
import net.minecraft.state.property.Properties;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.util.FeatureContext;

import java.util.Iterator;
import java.util.Optional;
import java.util.Random;

public abstract class DeadCoralFeature extends Feature<DefaultFeatureConfig> {
    public DeadCoralFeature(Codec<DefaultFeatureConfig> codec) {
        super(codec);
    }

    public boolean generate(FeatureContext<DefaultFeatureConfig> context) {
        Random random = context.getRandom();
        StructureWorldAccess structureWorldAccess = context.getWorld();
        BlockPos blockPos = context.getOrigin();

        Optional<Block> optional = Registry.BLOCK.getEntryList(TGTags.DEAD_CORAL_BLOCKS).flatMap((blocks) -> {
            return blocks.getRandom(random);
        }).map(RegistryEntry::value);
        return optional.isEmpty() ? false : this.generateCoral(structureWorldAccess, random, blockPos, ((Block)optional.get()).getDefaultState());
    }

    protected abstract boolean generateCoral(WorldAccess world, Random random, BlockPos pos, BlockState state);

    protected boolean generateCoralPiece(WorldAccess world, Random random, BlockPos pos, BlockState state) {
        BlockPos blockPos = pos.up();
        BlockState blockState = world.getBlockState(pos);
        if (world.getBlockState(pos.down()).isOf(Blocks.WATER) || world.getBlockState(pos.down()).isOf(Blocks.AIR) || !world.getBlockState(pos.down()).isSolidBlock(world, pos.down())) {
            return false;
        }

        if (((blockState.isOf(Blocks.AIR) || blockState.isOf(Blocks.WATER)) || blockState.isIn(TGTags.DEAD_CORALS)) && (world.getBlockState(blockPos).isOf(Blocks.AIR) || world.getBlockState(blockPos).isOf(Blocks.WATER))) {
            world.setBlockState(pos, state, 3);
            if (random.nextFloat() < 0.25F) {

                if (world.getBlockState(blockPos).isOf(Blocks.WATER)) {
                    Registry.BLOCK.getEntryList(TGTags.DEAD_CORALS).flatMap((blocks) -> {
                        return blocks.getRandom(random);
                    }).map(RegistryEntry::value).ifPresent((block) -> {
                        world.setBlockState(blockPos, block.getDefaultState().with(Properties.WATERLOGGED, true), 2);
                    });
                } else if (world.getBlockState(blockPos).isOf(Blocks.AIR)) {
                    Registry.BLOCK.getEntryList(TGTags.DEAD_CORALS).flatMap((blocks) -> {
                        return blocks.getRandom(random);
                    }).map(RegistryEntry::value).ifPresent((block) -> {
                        world.setBlockState(blockPos, block.getDefaultState().with(Properties.WATERLOGGED, false), 2);
                    });
                }
            } //else if (random.nextFloat() < 0.05F) {
            //    world.setBlockState(blockPos, (BlockState)Blocks.SEA_PICKLE.getDefaultState().with(SeaPickleBlock.PICKLES, random.nextInt(4) + 1), 2);
            //}

            Iterator var7 = Direction.Type.HORIZONTAL.iterator();

            while(var7.hasNext()) {
                Direction direction = (Direction)var7.next();
                if (random.nextFloat() < 0.2F) {
                    BlockPos blockPos2 = pos.offset(direction);
                    if (world.getBlockState(blockPos2).isOf(Blocks.WATER)) {
                        Registry.BLOCK.getEntryList(TGTags.DEAD_WALL_CORALS).flatMap((blocks) -> {
                            return blocks.getRandom(random);
                        }).map(RegistryEntry::value).ifPresent((block) -> {
                            BlockState blockState2 = block.getDefaultState().with(Properties.WATERLOGGED, true);
                            if (blockState2.contains(DeadCoralWallFanBlock.FACING)) {
                                blockState2 = (BlockState)blockState2.with(DeadCoralWallFanBlock.FACING, direction);
                            }

                            world.setBlockState(blockPos2, blockState2, 2);
                        });
                    } else if (world.getBlockState(blockPos2).isOf(Blocks.AIR)) {
                        Registry.BLOCK.getEntryList(TGTags.DEAD_WALL_CORALS).flatMap((blocks) -> {
                            return blocks.getRandom(random);
                        }).map(RegistryEntry::value).ifPresent((block) -> {
                            BlockState blockState2 = block.getDefaultState().with(Properties.WATERLOGGED, false);
                            if (blockState2.contains(DeadCoralWallFanBlock.FACING)) {
                                blockState2 = (BlockState)blockState2.with(DeadCoralWallFanBlock.FACING, direction);
                            }

                            world.setBlockState(blockPos2, blockState2, 2);
                        });
                    }
                }
            }

            return true;
        } else {
            return false;
        }
    }
}

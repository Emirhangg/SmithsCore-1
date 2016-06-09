package com.smithsmodding.smithscore.client.model.unbaked;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.smithsmodding.smithscore.client.model.baked.BakedMultiComponentModel;
import com.smithsmodding.smithscore.client.model.overrides.PreBakedComponentOverrideList;
import com.smithsmodding.smithscore.client.model.overrides.PreBakedItemOverride;
import com.smithsmodding.smithscore.util.client.ModelHelper;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.common.model.IModelState;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;


/**
 * @Author Marc (Created on: 29.05.2016)
 */
public class MultiComponentModel implements IModel {

    final ImmutableMap<String, IModel> components;

    public MultiComponentModel(ImmutableMap<String, IModel> components) {
        this.components = components;
    }

    @Override
    public Collection<ResourceLocation> getDependencies() {
        return Collections.EMPTY_LIST;
    }

    @Override
    public Collection<ResourceLocation> getTextures() {
        ImmutableList.Builder<ResourceLocation> builder = new ImmutableList.Builder<ResourceLocation>();
        for (IModel component : components.values())
            builder.addAll(component.getTextures());

        return builder.build();
    }

    @Override
    public IBakedModel bake(IModelState state, VertexFormat format, Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter) {
        ImmutableList.Builder<PreBakedItemOverride> builder = new ImmutableList.Builder<>();

        for (Map.Entry<String, IModel> component : components.entrySet()) {
            builder.add(new BakedMultiComponentModel.BakedComponentModelItemOverride(component.getValue().bake(state, format, bakedTextureGetter), component.getKey()));
        }

        ImmutableList<PreBakedItemOverride> overrides = builder.build();
        if (overrides.isEmpty())
            return DummyModel.INSTANCE.bake(state, format, bakedTextureGetter);

        return new BakedMultiComponentModel(overrides.get(0).getModel().getParticleTexture(), overrides.get(0).getModel().getItemCameraTransforms(), new PreBakedComponentOverrideList(overrides));
    }

    @Override
    public IModelState getDefaultState() {
        return ModelHelper.DEFAULT_ITEM_STATE;
    }
}

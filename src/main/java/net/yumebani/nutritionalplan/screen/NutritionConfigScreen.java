package net.yumebani.nutritionalplan.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.yumebani.nutritionalplan.ModConfigValues;
import net.yumebani.nutritionalplan.NutritionalPlanMod;
import net.yumebani.nutritionalplan.data.NutritionDataClient;
import net.yumebani.nutritionalplan.data.NutritionUpgrades;
import net.yumebani.nutritionalplan.data.NutritionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

public class NutritionConfigScreen extends Screen {

    private static final ResourceLocation NORMAL_BUTTON = ResourceLocation.fromNamespaceAndPath(NutritionalPlanMod.MODID, "textures/widgets/upgrade.png");
    private static final ResourceLocation SHIFT_BUTTON = ResourceLocation.fromNamespaceAndPath(NutritionalPlanMod.MODID, "textures/widgets/unupgrade.png");
    private static final ResourceLocation DISABLED_BUTTON = ResourceLocation.fromNamespaceAndPath(NutritionalPlanMod.MODID, "textures/widgets/disabled.png");

    private static final WidgetSprites SPRITES = new WidgetSprites(NORMAL_BUTTON, SHIFT_BUTTON, DISABLED_BUTTON);

    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(NutritionalPlanMod.MODID, "textures/gui/nutritional_planner.png");

    private final int imageWidth = 176;
    private final int imageHeight = 101;

    private boolean lastShift = false;
    private boolean isCooldown = false;

    private ShiftImageButton healthButton;
    private ShiftImageButton regenButton;
    private ShiftImageButton speedButton;
    private ShiftImageButton hasteButton;
    private ShiftImageButton strengthButton;
    private ShiftImageButton resistanceButton;

    private List<ShiftImageButton> buttons;

    public NutritionConfigScreen() {
        super(Component.literal(""));
    }

    @Override
    protected void init() {

        super.init();

        healthButton = new ShiftImageButton(37, 36, 22, 22, 1.4f, this.imageWidth, this.imageHeight, NORMAL_BUTTON, SHIFT_BUTTON, DISABLED_BUTTON, SPRITES, this::healthButtonClick, Component.literal("Max Health Upgrade"));
        this.addRenderableWidget(healthButton);
        regenButton = new ShiftImageButton(89, 36, 22, 22, 1.4f, this.imageWidth, this.imageHeight,NORMAL_BUTTON, SHIFT_BUTTON, DISABLED_BUTTON, SPRITES, this::regenButtonClick, Component.literal("Regen Upgrade"));
        this.addRenderableWidget(regenButton);
        speedButton = new ShiftImageButton(141, 36, 22, 22, 1.4f, this.imageWidth, this.imageHeight,NORMAL_BUTTON, SHIFT_BUTTON, DISABLED_BUTTON, SPRITES, this::speedButtonClick, Component.literal("Speed Upgrade"));
        this.addRenderableWidget(speedButton);
        hasteButton = new ShiftImageButton(37, 68, 22, 22, 1.4f, this.imageWidth, this.imageHeight,NORMAL_BUTTON, SHIFT_BUTTON, DISABLED_BUTTON, SPRITES, this::hasteButtonClick, Component.literal("Haste Upgrade"));
        this.addRenderableWidget(hasteButton);
        strengthButton = new ShiftImageButton(89, 68, 22, 22, 1.4f, this.imageWidth, this.imageHeight,NORMAL_BUTTON, SHIFT_BUTTON, DISABLED_BUTTON, SPRITES, this::strengthButtonClick, Component.literal("Strength Upgrade"));
        this.addRenderableWidget(strengthButton);
        resistanceButton = new ShiftImageButton(141, 68, 22, 22, 1.4f, this.imageWidth, this.imageHeight,NORMAL_BUTTON, SHIFT_BUTTON, DISABLED_BUTTON, SPRITES, this::resistanceButtonClick, Component.literal("Resistance Upgrade"));
        this.addRenderableWidget(resistanceButton);

        buttons = new ArrayList<>();
        buttons.add(healthButton);
        buttons.add(regenButton);
        buttons.add(speedButton);
        buttons.add(hasteButton);
        buttons.add(strengthButton);
        buttons.add(resistanceButton);

        if (NutritionDataClient.getInstance().isOnCooldown()){
            configureOnCooldownDefault();
        } else {
            configureDefault();
        }
    }

    public void healthButtonClick(Button button){
        NutritionDataClient data = NutritionDataClient.getInstance();
        Player player = Minecraft.getInstance().player;
        player.level().playSound(player, player.blockPosition(), SoundEvents.BOOK_PAGE_TURN, SoundSource.PLAYERS);
        if (Screen.hasShiftDown()){
            data.unspendPoints(NutritionUpgrades.HEALTH);
            configureShiftHealth();
        } else {
            data.spendPoints(NutritionUpgrades.HEALTH);
            configureDefaultHealth();
        }
    }

    public void regenButtonClick(Button button){
        NutritionDataClient data = NutritionDataClient.getInstance();
        Player player = Minecraft.getInstance().player;
        player.level().playSound(player, player.blockPosition(), SoundEvents.BOOK_PAGE_TURN, SoundSource.PLAYERS);
        if (Screen.hasShiftDown()){
            data.unspendPoints(NutritionUpgrades.REGENERATION);
            configureShiftRegen();
        } else {
            data.spendPoints(NutritionUpgrades.REGENERATION);
            configureDefaultRegen();
        }
    }

    public void speedButtonClick(Button button){
        NutritionDataClient data = NutritionDataClient.getInstance();
        Player player = Minecraft.getInstance().player;
        player.level().playSound(player, player.blockPosition(), SoundEvents.BOOK_PAGE_TURN, SoundSource.PLAYERS);
        if (Screen.hasShiftDown()){
            data.unspendPoints(NutritionUpgrades.SPEED);
            configureShiftSpeed();
        } else {
            data.spendPoints(NutritionUpgrades.SPEED);
            configureDefaultSpeed();
        }
    }

    public void hasteButtonClick(Button button){
        NutritionDataClient data = NutritionDataClient.getInstance();
        Player player = Minecraft.getInstance().player;
        player.level().playSound(player, player.blockPosition(), SoundEvents.BOOK_PAGE_TURN, SoundSource.PLAYERS);
        if (Screen.hasShiftDown()){
            data.unspendPoints(NutritionUpgrades.HASTE);
            configureShiftHaste();
        } else {
            data.spendPoints(NutritionUpgrades.HASTE);
            configureDefaultHaste();
        }
    }

    public void strengthButtonClick(Button button){
        NutritionDataClient data = NutritionDataClient.getInstance();
        Player player = Minecraft.getInstance().player;
        player.level().playSound(player, player.blockPosition(), SoundEvents.BOOK_PAGE_TURN, SoundSource.PLAYERS);
        if (Screen.hasShiftDown()){
            data.unspendPoints(NutritionUpgrades.STRENGTH);
            configureShiftStrength();
        } else {
            data.spendPoints(NutritionUpgrades.STRENGTH);
            configureDefaultStrength();
        }
    }

    public void resistanceButtonClick(Button button){
        NutritionDataClient data = NutritionDataClient.getInstance();
        Player player = Minecraft.getInstance().player;
        player.level().playSound(player, player.blockPosition(), SoundEvents.BOOK_PAGE_TURN, SoundSource.PLAYERS);
        if (Screen.hasShiftDown()){
            data.unspendPoints(NutritionUpgrades.RESISTANCE);
            configureShiftResistance();
        } else {
            data.spendPoints(NutritionUpgrades.RESISTANCE);
            configureDefaultResistance();
        }
    }

    public void configureOnCooldownDefault(){
        for (ShiftImageButton button : this.buttons){
            button.setTooltip(Tooltip.create(Component.literal("Upgrades on cooldown")));
            button.active = false;
        }
    }

    public void configureDefault(){
        this.configureDefaultHealth();
        this.configureDefaultRegen();
        this.configureDefaultSpeed();
        this.configureDefaultHaste();
        this.configureDefaultStrength();
        this.configureDefaultResistance();
    }

    public void configureDefaultHealth(){
        configureDefaultButton(this.healthButton, NutritionUpgrades.HEALTH, "health");
    }

    public void configureDefaultRegen(){
        configureDefaultButton(this.regenButton, NutritionUpgrades.REGENERATION, "regeneration");
    }

    public void configureDefaultSpeed(){
        configureDefaultButton(this.speedButton, NutritionUpgrades.SPEED, "speed");
    }

    public void configureDefaultHaste(){
        configureDefaultButton(this.hasteButton, NutritionUpgrades.HASTE, "haste");
    }

    public void configureDefaultStrength(){
        configureDefaultButton(this.strengthButton, NutritionUpgrades.STRENGTH, "strength");
    }

    public void configureDefaultResistance(){
        configureDefaultButton(this.resistanceButton, NutritionUpgrades.RESISTANCE, "resistance");
    }

    public void configureDefaultButton(ShiftImageButton button, NutritionUpgrades upgrade, String text){
        NutritionDataClient data = NutritionDataClient.getInstance();
        if (data.canUpgrade(upgrade)){
            button.setTooltip(Tooltip.create(Component.literal("Upgrade max " + text + "\nCosts " + data.getPointCost(upgrade) + " points" )));
            button.active = true;
        } else if (data.isMaxLevel(upgrade)){
            button.setTooltip(Tooltip.create(Component.literal("Max " + text + " level achieved")));
            button.active = false;
        } else {
            button.setTooltip(Tooltip.create(Component.literal("Upgrade max " + text + "\nCosts " + data.getPointCost(upgrade) + " points\nInsufficient points" )));
            button.active = false;
        }
    }

    public void configureShift(){
        this.configureShiftHealth();
        this.configureShiftRegen();
        this.configureShiftSpeed();
        this.configureShiftHaste();
        this.configureShiftStrength();
        this.configureShiftResistance();
    }

    private void configureShiftHealth() {
        configureShiftButton(this.healthButton, "health", NutritionUpgrades.HEALTH);
    }

    private void configureShiftRegen() {
        configureShiftButton(this.regenButton, "regen", NutritionUpgrades.REGENERATION);
    }

    private void configureShiftSpeed() {
        configureShiftButton(this.speedButton, "speed", NutritionUpgrades.SPEED);
    }

    private void configureShiftHaste() {
        configureShiftButton(this.hasteButton, "haste", NutritionUpgrades.HASTE);
    }

    private void configureShiftStrength() {
        configureShiftButton(this.strengthButton, "strength", NutritionUpgrades.STRENGTH);
    }

    private void configureShiftResistance() {
        configureShiftButton(this.resistanceButton, "resistance", NutritionUpgrades.RESISTANCE);
    }

    public void configureShiftButton(ShiftImageButton button, String text, NutritionUpgrades upgrade){
        NutritionDataClient data = NutritionDataClient.getInstance();
        if (data.canUnupgrade(upgrade)){
            button.setTooltip(Tooltip.create(Component.literal("Refund max " + text + "\nRetrieved " + data.getPointCostPrev(upgrade) + " points" )));
            button.active = true;
        } else {
            button.setTooltip(Tooltip.create(Component.literal("Min " + text + " level")));
            button.active = false;
        }
    }

    @Override
    public void tick() {
        super.tick();
        NutritionDataClient data = NutritionDataClient.getInstance();
        if (data.isOnCooldown() && !this.isCooldown){
            configureOnCooldownDefault();
            this.isCooldown = true;
        } else if (!data.isOnCooldown() && this.isCooldown) {
            this.isCooldown = false;
            if (Screen.hasShiftDown()){
                configureShift();
                this.lastShift = true;
            } else if (!Screen.hasShiftDown()){
                configureDefault();
                this.lastShift = false;
            }
        } else if (!this.isCooldown && Screen.hasShiftDown() && this.lastShift == false){
            configureShift();
            this.lastShift = true;
        } else if (!this.isCooldown && !Screen.hasShiftDown() && this.lastShift == true){
            configureDefault();
            this.lastShift = false;
        }
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.renderBackground(guiGraphics, mouseX, mouseY, partialTick);

        float UIScale = 1.4f;

        float scaledLeft = ((this.width - (this.imageWidth * UIScale)) / 2.0F);
        float scaledTop  = ((this.height - (this.imageHeight * UIScale)) / 2.0F);

        int realMouseX = (int)((mouseX - scaledLeft) / UIScale);
        int realMouseY = (int)((mouseY - scaledTop) / UIScale);

        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(scaledLeft, scaledTop, 0.0F);
        guiGraphics.pose().scale(UIScale, UIScale, 1.0F);

        guiGraphics.blit(TEXTURE, 0, 0, 0, 0, this.imageWidth, this.imageHeight);

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

        for (ShiftImageButton button : this.buttons) {
            button.render(guiGraphics, realMouseX, realMouseY, partialTick);
        }

        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.disableBlend();

        NutritionDataClient data = NutritionDataClient.getInstance();

        if (data.getLevel() != NutritionUtils.getMaxLevel()){
            guiGraphics.blit(TEXTURE, 10, 24, 0, 101, (int)(156 * ((float)(data.getExp()) / NutritionUtils.getExpForLevel(data.getLevel()))), 6);
        } else {
            guiGraphics.blit(TEXTURE, 10, 24, 0, 101, 156, 6);

        }

        guiGraphics.pose().popPose();
        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(scaledLeft, scaledTop, 0.0F);
        guiGraphics.pose().scale(0.8f, 0.8f, 0.8f);

        float textUIScale = UIScale / 0.8f;

        guiGraphics.drawCenteredString(this.font, Component.literal("Level: " + data.getLevel() + "   Pts:" + data.getPoints()),(int)(34 * textUIScale), (int)(14 * textUIScale) , 0x6a5b47);
        guiGraphics.drawCenteredString(this.font, Component.literal("Cooldown: " + data.getCooldownFormated()),(int)(143 * textUIScale), (int)(14 * textUIScale), 0x6a5b47);

        guiGraphics.drawCenteredString(this.font, Component.literal("Level: " + data.getNutritionUpgrade(NutritionUpgrades.HEALTH)),(int)(21  * textUIScale), (int)(38 * textUIScale), 0x6a5b47);
        Component healthText = data.getNutritionUpgrade(NutritionUpgrades.HEALTH) != 0 ?
                Component.literal("+" + data.getNutritionUpgrade(NutritionUpgrades.HEALTH) * ModConfigValues.CONFIG.healthUpgradeHealthPerLevel.get()) :
                Component.literal("None");
        guiGraphics.drawCenteredString(this.font, healthText,(int)(21 * textUIScale), (int)(48 * textUIScale), 0x6a5b47);

        guiGraphics.drawCenteredString(this.font, Component.literal("Level: " + data.getNutritionUpgrade(NutritionUpgrades.REGENERATION)),(int)(73 * textUIScale), (int)(38 * textUIScale), 0x6a5b47);
        Component regenText = data.getNutritionUpgrade(NutritionUpgrades.REGENERATION) != 0 ?
                Component.literal(String.format(Locale.ROOT,"%.1f", (ModConfigValues.CONFIG.regenUpgradeBaseInterval.get() - data.getNutritionUpgrade(NutritionUpgrades.REGENERATION) * ModConfigValues.CONFIG.regenUpgradeIntervalDecrease.get()) / 20f) + "s") :
                Component.literal("None");
        guiGraphics.drawCenteredString(this.font, regenText,(int)(73 * textUIScale), (int)(48 * textUIScale), 0x6a5b47);

        guiGraphics.drawCenteredString(this.font, Component.literal("Level: " + data.getNutritionUpgrade(NutritionUpgrades.SPEED)),(int)(125 * textUIScale), (int)(38 * textUIScale), 0x6a5b47);
        Component speedText = data.getNutritionUpgrade(NutritionUpgrades.SPEED) != 0 ?
                Component.literal("+" + String.format(Locale.ROOT,"%.1f", (data.getNutritionUpgrade(NutritionUpgrades.SPEED) * ModConfigValues.CONFIG.speedUpgradeSpeedPerLevel.get() * 100)) + "%") :
                Component.literal("None");
        guiGraphics.drawCenteredString(this.font, speedText,(int)(125 * textUIScale), (int)(48 * textUIScale), 0x6a5b47);

        guiGraphics.drawCenteredString(this.font, Component.literal("Level: " + data.getNutritionUpgrade(NutritionUpgrades.HASTE)),(int)(21 * textUIScale), (int)(70 * textUIScale), 0x6a5b47);
        Component hasteText = data.getNutritionUpgrade(NutritionUpgrades.HASTE) != 0 ?
                Component.literal("+" + String.format(Locale.ROOT,"%.1f", (data.getNutritionUpgrade(NutritionUpgrades.HASTE) * ModConfigValues.CONFIG.hasteUpgradeSpeedPerLevel.get() * 100)) + "%") :
                Component.literal("None");
        guiGraphics.drawCenteredString(this.font, hasteText,(int)(21 * textUIScale), (int)(80 * textUIScale), 0x6a5b47);

        guiGraphics.drawCenteredString(this.font, Component.literal("Level: " + data.getNutritionUpgrade(NutritionUpgrades.STRENGTH)),(int)(73 * textUIScale), (int)(70 * textUIScale), 0x6a5b47);
        Component strengthText = data.getNutritionUpgrade(NutritionUpgrades.STRENGTH) != 0 ?
                Component.literal("+" + String.format(Locale.ROOT,"%.1f", (data.getNutritionUpgrade(NutritionUpgrades.STRENGTH) * ModConfigValues.CONFIG.strengthUpgradeDmgPerLevel.get() * 100)) + "%") :
                Component.literal("None");
        guiGraphics.drawCenteredString(this.font, strengthText,(int)(73 * textUIScale), (int)(80 * textUIScale), 0x6a5b47);

        guiGraphics.drawCenteredString(this.font, Component.literal("Level: " + data.getNutritionUpgrade(NutritionUpgrades.RESISTANCE)),(int)(125 * textUIScale), (int)(70 * textUIScale), 0x6a5b47);
        Component resistanceText = data.getNutritionUpgrade(NutritionUpgrades.RESISTANCE) != 0 ?
                Component.literal("+" + String.format(Locale.ROOT,"%.1f", (data.getNutritionUpgrade(NutritionUpgrades.RESISTANCE) * ModConfigValues.CONFIG.resistanceUpgradeReductionPerLevel.get() * 100)) + "%") :
                Component.literal("None");
        guiGraphics.drawCenteredString(this.font, resistanceText,(int)(125 * textUIScale), (int)(80 * textUIScale), 0x6a5b47);

        guiGraphics.pose().popPose();

        float areaX = 10;
        float areaY = 24;
        int areaWidth = 156;
        int areaHeight = 6;

        if (realMouseX > areaX && realMouseX < (areaX + areaWidth) && realMouseY > areaY && realMouseY < (areaY + areaHeight)) {
            List<Component> components = data.getLevel() != NutritionUtils.getMaxLevel() ? List.of(Component.literal(data.getExp() + "/" + NutritionUtils.getExpForLevel(data.getLevel()))) : List.of(Component.literal("Max level"));
            guiGraphics.renderTooltip(this.font, List.of(Component.literal(data.getExp() + "/" + NutritionUtils.getExpForLevel(data.getLevel()))), Optional.empty(), mouseX, mouseY);

        }

    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    public static void open() {
        Minecraft.getInstance().setScreen(new NutritionConfigScreen());
    }
}

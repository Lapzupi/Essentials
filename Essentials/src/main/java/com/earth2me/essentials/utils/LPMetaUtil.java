package com.earth2me.essentials.utils;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.cacheddata.CachedMetaData;
import net.luckperms.api.node.NodeType;
import net.luckperms.api.node.types.MetaNode;

import java.util.UUID;

/**
 * @author sarhatabaot
 */
public final class LPMetaUtil {
    private static final String META_KEY = "essentials_homes";
    private static final String BASE_META_KEY = META_KEY + ".base-amount";
    private static final String BONUS_META_KEY = META_KEY + ".bonus-amount";
    private static final LuckPerms luckPerms = LuckPermsProvider.get();

    private LPMetaUtil() {
        throw new UnsupportedOperationException();
    }

    public static void setBonusHomeAmount(final UUID uuid, final int amount) {
        final net.luckperms.api.model.user.User user = luckPerms.getUserManager().getUser(uuid);
        if (user == null)
            return;

        final MetaNode node = MetaNode.builder()
                .key(BONUS_META_KEY)
                .value(String.valueOf(amount))
                .build();

        user.data().clear(NodeType.META.predicate(mn -> mn.getMetaKey().equals(BONUS_META_KEY)));
        user.data().add(node);

        luckPerms.getUserManager().saveUser(user);
    }

    public static void addBonusHomeAmount(final UUID uuid, final int amount) {
        final net.luckperms.api.model.user.User user = luckPerms.getUserManager().getUser(uuid);
        if (user == null)
            return;

        final int currentAmount = getBonusHomeAmount(uuid);
        final int newAmount = currentAmount + amount;

        final MetaNode node = MetaNode.builder()
                .key(BONUS_META_KEY)
                .value(String.valueOf(newAmount))
                .build();

        user.data().clear(NodeType.META.predicate(mn -> mn.getMetaKey().equals(BONUS_META_KEY)));
        user.data().add(node);

        luckPerms.getUserManager().saveUser(user);
    }

    public static void removeBonusHomeAmount(final UUID uuid, final int amount) {
        final net.luckperms.api.model.user.User user = luckPerms.getUserManager().getUser(uuid);
        if (user == null)
            return;

        final int currentAmount = getBonusHomeAmount(uuid);
        int newAmount = currentAmount - amount;
        if (newAmount < 0) {
            newAmount = 0;
        }

        final MetaNode node = MetaNode.builder()
                .key(BONUS_META_KEY)
                .value(String.valueOf(newAmount))
                .build();

        user.data().clear(NodeType.META.predicate(mn -> mn.getMetaKey().equals(BONUS_META_KEY)));
        user.data().add(node);

        luckPerms.getUserManager().saveUser(user);
    }

    public static int calcMaxHomes(final UUID uuid) {
        return getBaseHomeAmount(uuid) + getBonusHomeAmount(uuid);
    }

    public static int getBaseHomeAmount(final UUID uuid) {
        final net.luckperms.api.model.user.User user = luckPerms.getUserManager().getUser(uuid);
        if (user == null)
            return -1;

        final CachedMetaData metaData = user.getCachedData().getMetaData();

        return metaData.getMetaValue(BASE_META_KEY, Integer::parseInt).orElse(0);
    }

    public static int getBonusHomeAmount(final UUID uuid) {
        final net.luckperms.api.model.user.User user = luckPerms.getUserManager().getUser(uuid);
        if (user == null)
            return -1;

        final CachedMetaData metaData = user.getCachedData().getMetaData();

        return metaData.getMetaValue(BONUS_META_KEY, Integer::parseInt).orElse(0);
    }
}

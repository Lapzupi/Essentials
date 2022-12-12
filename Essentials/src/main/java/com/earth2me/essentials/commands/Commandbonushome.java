package com.earth2me.essentials.commands;

import com.earth2me.essentials.User;
import com.earth2me.essentials.utils.LPMetaUtil;
import org.bukkit.Server;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import static com.earth2me.essentials.I18n.tl;

public class Commandbonushome extends EssentialsCommand {

    public Commandbonushome() {
        super("bonushome");
    }

    @Override
    protected void run(final Server server, final User user, final String commandLabel, final String[] args) throws Exception {
        switch (args.length) {
            case 0:
                if (!user.isAuthorized("essentials.bonushome.info")) {
                    throw new Exception(tl("noPerm", "essentials.bonushome.info"));
                }
                user.sendMessage(tl("bonushomeInfo", LPMetaUtil.calcMaxHomes(user.getUUID()), LPMetaUtil.getBaseHomeAmount(user.getUUID()), LPMetaUtil.getBonusHomeAmount(user.getUUID())));
                break;
            case 1:
            case 2:
                throw new NotEnoughArgumentsException();
            case 3: {
                if (!user.isAuthorized("essentials.bonushome.edit")) {
                    throw new Exception(tl("noPerm", "essentials.bonushome.edit"));
                }
                final User target = getPlayer(server, user, args, 1);
                final int amount = Integer.parseInt(args[2]);
                if (amount <= 0) {
                    throw new Exception(tl("bonushomeGreaterThan",""));
                }

                final String mode = args[0];

                switch (mode.toLowerCase(Locale.ENGLISH)) {
                    case "add": {
                        LPMetaUtil.addBonusHomeAmount(target.getUUID(), amount);
                        user.sendMessage(tl("bonushomeAdd",amount, user.getName()));
                        return;
                    }
                    case "remove": {
                        LPMetaUtil.removeBonusHomeAmount(target.getUUID(), amount);
                        user.sendMessage(tl("bonushomeRemove", amount, user.getName()));
                        return;
                    }
                    case "set": {
                        LPMetaUtil.setBonusHomeAmount(target.getUUID(), amount);
                        user.sendMessage(tl("bonushomeSet", amount, user.getName()));
                        return;
                    }
                    default: {
                        throw new Exception("No such argument. Use add, set, remove");
                    }
                }
            }
            default:
                throw new Exception("Too many arguments");
        }
    }

    @Override
    protected List<String> getTabCompleteOptions(final Server server, final User user, final String commandLabel, final String[] args) {
        if (args.length == 1) {
            final String[] tabCompletion = new String[]{"add", "remove", "set"};
            return Arrays.stream(tabCompletion).collect(Collectors.toList());
        }
        if (args.length == 2) {
            return getPlayers(server, user);
        }
        return Collections.emptyList();
    }
}

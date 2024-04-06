package com.earth2me.essentials.commands;

import com.earth2me.essentials.CommandSource;
import com.earth2me.essentials.User;
import com.earth2me.essentials.utils.LPMetaUtil;
import net.ess3.api.TranslatableException;
import org.bukkit.Server;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class Commandbonushome extends EssentialsCommand {

    public Commandbonushome() {
        super("bonushome");
    }

    private User getUser(Server server, String[] args, Player player) throws Exception {
        if (player == null) {
            return getPlayer(server, args, 1, true, true);
        }
        return ess.getUser(player);
    }

    @Override
    protected void run(Server server, CommandSource sender, String commandLabel, String[] args) throws Exception {
        final User user = getUser(server, args, sender.getPlayer());

        if (user == null) {
            throw new Exception("Could not get user.");
        }

        switch (args.length) {
            case 0:
                sendInfoMessage(sender, user);
                return;
            case 1:
                throw new NotEnoughArgumentsException();
            case 2: {
                final String mode = args[0];
                if (!mode.equalsIgnoreCase("info")) {
                    throw new NotEnoughArgumentsException();
                }

                if (!sender.isAuthorized("essentials.bonushome.info.others")) {
                    throw new TranslatableException("noPerm", "essentials.bonushome.info.others");
                }

                final User target = getPlayer(server, user, args, 1);
                sendInfoMessage(sender, target);
                return;
            }
            case 3: {
                if (!sender.isAuthorized("essentials.bonushome.edit")) {
                    throw new TranslatableException("noPerm", "essentials.bonushome.edit");
                }
                final User target = getPlayer(server, user, args, 1);
                final int amount = Integer.parseInt(args[2]);
                final String mode = args[0];

                switch (mode.toLowerCase(Locale.ENGLISH)) {
                    case "add": {
                        if (amount <= 0) {
                            throw new TranslatableException("bonushomeGreaterThan", "");
                        }
                        LPMetaUtil.addBonusHomeAmount(target.getUUID(), amount);
                        user.sendTl("bonushomeAdd", amount, user.getName());
                        return;
                    }
                    case "remove": {
                        if (amount <= 0) {
                            throw new TranslatableException("bonushomeGreaterThan", "");
                        }
                        LPMetaUtil.removeBonusHomeAmount(target.getUUID(), amount);
                        user.sendTl("bonushomeRemove", amount, user.getName());
                        return;
                    }
                    case "set": {
                        if (amount < 0) {
                            throw new TranslatableException("bonushomeGreaterThan", "");
                        }
                        LPMetaUtil.setBonusHomeAmount(target.getUUID(), amount);
                        user.sendTl("bonushomeSet", amount, user.getName());
                        return;
                    }
                    default: {
                        throw new Exception("No such argument. Use add, set, remove, info.");
                    }
                }
            }
            default:
                throw new Exception("Too many arguments");
        }
    }

    private void sendInfoMessage(final CommandSource sender, final User target) throws TranslatableException {
        if (!sender.isAuthorized("essentials.bonushome.info")) {
            throw new TranslatableException("noPerm", "essentials.bonushome.info");
        }

        sender.sendTl("bonushomeInfo", LPMetaUtil.calcMaxHomes(target.getUUID()), LPMetaUtil.getBaseHomeAmount(target.getUUID()), LPMetaUtil.getBonusHomeAmount(target.getUUID()));
    }

    @Override
    protected List<String> getTabCompleteOptions(final Server server, final User user, final String commandLabel, final String[] args) {
        if (args.length == 1) {
            final String[] tabCompletion = getTabCompletion(user);
            return Arrays.stream(tabCompletion).collect(Collectors.toList());
        }
        if (args.length == 2) {
            return getPlayers(server, user);
        }
        return Collections.emptyList();
    }

    private String[] getTabCompletion(final User user) {
        if (!user.isAuthorized("essentials.bonushome.edit")) {
            if (user.isAuthorized("essentials.bonushome.info.others")) {
                return new String[]{"info"};
            }
            return new String[]{""};
        }
        return new String[]{"add", "remove", "set", "info"};
    }
}

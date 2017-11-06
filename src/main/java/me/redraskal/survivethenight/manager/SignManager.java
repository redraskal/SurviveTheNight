package me.redraskal.survivethenight.manager;

import lombok.Getter;
import me.redraskal.survivethenight.SurviveTheNight;
import me.redraskal.survivethenight.game.Arena;
import me.redraskal.survivethenight.game.GameState;
import me.redraskal.survivethenight.game.SignInfo;
import me.redraskal.survivethenight.listener.SignListener;
import me.redraskal.survivethenight.utils.ConfigUtils;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.ArrayList;
import java.util.List;

/**
 * Copyright (c) Redraskal 2017.
 * <p>
 * Please do not copy the code below unless you
 * have permission to do so from me.
 */
public class SignManager {

    @Getter private final SurviveTheNight surviveTheNight;
    @Getter private final SignListener signListener;
    @Getter private List<SignInfo> signInfoList = new ArrayList<>();

    public SignManager(SurviveTheNight surviveTheNight) {
        this.surviveTheNight = surviveTheNight;
        this.signListener = new SignListener(this);
        this.getSurviveTheNight().getServer().getPluginManager()
                .registerEvents(this.getSignListener(), this.getSurviveTheNight());
        this.reloadSigns();
    }

    public void reloadSigns() {
        this.getSignInfoList().clear();
        YamlConfiguration signConfig = this.getSurviveTheNight().getSignConfig();
        signConfig.getStringList("signs").forEach(key -> {
            String[] arr = key.split(";");
            Block block = ConfigUtils.decodeLocation(arr[0]).getBlock();
            if(block.getType().toString().contains("SIGN")) {
                Sign sign = (Sign) block.getState();
                int arenaID = Integer.parseInt(arr[1]);
                SignInfo.SignType signType = SignInfo.SignType.valueOf(arr[2]);
                this.getSignInfoList().add(new SignInfo(sign, arenaID, signType));
            }
        });
    }

    public boolean createSign(SignInfo signInfo) {
        if(this.getSignInfoList().contains(signInfo)) return false;

        this.getSignInfoList().add(signInfo);
        this.saveSigns();
        this.updateSignState(signInfo);

        return true;
    }

    public boolean deleteSign(SignInfo signInfo) {
        if(!this.getSignInfoList().contains(signInfo)) return false;

        this.getSignInfoList().remove(signInfo);
        this.saveSigns();

        return true;
    }

    public void saveSigns() {
        YamlConfiguration signConfig = this.getSurviveTheNight().getSignConfig();
        List<String> signs = new ArrayList<>();

        this.getSignInfoList().forEach(signInfo -> signs.add(
                ConfigUtils.encodeLocation(signInfo.getSign().getLocation())
                        + ";" + signInfo.getArenaID()
                        + ";" + signInfo.getSignType().toString()));

        signConfig.set("signs", signs);
        this.getSurviveTheNight().saveSignConfig();
    }

    public void updateSigns(Arena arena) {
        this.getSignInfoList().forEach(signInfo -> {
            if(signInfo.getArenaID() == arena.getArenaid()) {
                this.updateSignState(signInfo);
            }
        });
    }

    private void updateSignState(SignInfo signInfo) {
        if(signInfo.getSignType() == SignInfo.SignType.JOIN) {
            Arena arena = this.getSurviveTheNight().getArenaManager()
                    .getArenaMap().get(signInfo.getArenaID());
            if(arena.getGameState() == GameState.LOBBY) {
                if(arena.getPlayers().size() >= arena.getMaxPlayers()) {
                    signInfo.getSign().setLine(0,
                            this.getSurviveTheNight().buildMessage("&5[Full-SP" + arena.getArenaid() + "]"));
                    signInfo.getSign().setLine(1,
                            this.getSurviveTheNight().buildMessage("Voting"));
                    signInfo.getSign().setLine(2,
                            this.getSurviveTheNight().buildMessage("&8&l"
                                    + arena.getPlayers().size() + "/" + arena.getMaxPlayers()));
                    signInfo.getSign().setLine(3,
                            this.getSurviveTheNight().buildMessage("&5&l• Lobby •"));
                } else {
                    signInfo.getSign().setLine(0,
                            this.getSurviveTheNight().buildMessage("&5[Join-SP" + arena.getArenaid() + "]"));
                    signInfo.getSign().setLine(1,
                            this.getSurviveTheNight().buildMessage("Voting"));
                    signInfo.getSign().setLine(2,
                            this.getSurviveTheNight().buildMessage("&8&l"
                                    + arena.getPlayers().size() + "/" + arena.getMaxPlayers()));
                    signInfo.getSign().setLine(3,
                            this.getSurviveTheNight().buildMessage("&5&l• Lobby •"));
                }
            } else {
                if(arena.getGameState() == GameState.FINISHED) {
                    signInfo.getSign().setLine(0,
                            this.getSurviveTheNight().buildMessage("&c■■■■■■■■■"));
                    signInfo.getSign().setLine(1,
                            this.getSurviveTheNight().buildMessage("&c[Restarting]"));
                    signInfo.getSign().setLine(2,
                            this.getSurviveTheNight().buildMessage("SP" + arena.getArenaid()));
                    signInfo.getSign().setLine(3,
                            this.getSurviveTheNight().buildMessage("&c■■■■■■■■■"));
                } else {
                    signInfo.getSign().setLine(0,
                            this.getSurviveTheNight().buildMessage("&7[NotJoinable]"));
                    signInfo.getSign().setLine(1,
                            this.getSurviveTheNight().buildMessage("SP" + arena.getArenaid()));
                    signInfo.getSign().setLine(2,
                            this.getSurviveTheNight().buildMessage("&8&l"
                                    + arena.getPlayers().size() + "/" + arena.getMaxPlayers()));
                    signInfo.getSign().setLine(3,
                            this.getSurviveTheNight().buildMessage("&8&l• In Game •"));
                }
            }
        } else {
            signInfo.getSign().setLine(0,
                    this.getSurviveTheNight().buildMessage("&c[Leave]"));
            signInfo.getSign().setLine(1,
                    this.getSurviveTheNight().buildMessage("Click to leave"));
            signInfo.getSign().setLine(2,
                    this.getSurviveTheNight().buildMessage(""));
            signInfo.getSign().setLine(3,
                    this.getSurviveTheNight().buildMessage(""));
        }
        signInfo.getSign().update(true);
    }
}
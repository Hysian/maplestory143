package tools;

import client.BuddyList;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

public final class RedisUtil {

    private static final Logger log = LogManager.getLogger();

    //Redis服务器IP
    private static final String ADDR = "127.0.0.1";

    //Redis的端口号
    private static final int PORT = 6379;
    //可用连接实例的最大数目，默认值为8；
    //如果赋值为-1，则表示不限制；如果pool已经分配了maxActive个jedis实例，则此时pool的状态为exhausted(耗尽)。
    private static final int MAX_ACTIVE = -1;
    //控制一个pool最多有多少个状态为idle(空闲的)的jedis实例，默认值也是8。
    private static final int MAX_IDLE = 200;
    //等待可用连接的最大时间，单位毫秒，默认值为-1，表示永不超时。如果超过等待时间，则直接抛出JedisConnectionException；
    private static final int MAX_WAIT = 10000;
    private static final int TIMEOUT = 10000;
    //在borrow一个jedis实例时，是否提前进行validate操作；如果为true，则得到的jedis实例均是可用的；
    private static final boolean TEST_ON_BORROW = true;
    //访问密码
    private static Cache<String, Object> cache = CacheBuilder.newBuilder().build();
    private static Table<String, String, String> htable = HashBasedTable.create();


    public static void hset(String key, String field, String value) {
        htable.put(key, field, value);
    }

    public static String hget(String key, String field) {
        return htable.get(key, field);
    }

    public static void hdel(String key, String field) {
        htable.remove(key, field);
    }

    public static boolean hexists(String key, String field) {
        return htable.containsRow(key) && htable.row(key).containsKey(field);
    }

    public static Map<String, String> hgetAll(String key) {
        return htable.row(key);
    }

    public static void del(String key) {
        if (htable.containsRow(key)) {
            htable.row(key).clear();
        }
    }

    public static boolean isMembers(String key, String value) {
        Set set = (Set) cache.getIfPresent(key);
        return set != null && set.contains(value);

    }

    public static boolean exists(String key) {
        return cache.getIfPresent(key) != null;
    }

    public static void flushall() {
    }

    public static Set<String> smembers(String key) {
        Object ifPresent = cache.getIfPresent(key);
        if (ifPresent == null) {
            cache.put(key, new LinkedHashSet<>());
        }
        Set ifPresent1 = (Set) cache.getIfPresent(key);
        return ifPresent1;
    }

    public static void hmset(String key, Map<String, String> values) {
        values.entrySet().forEach(entr -> {
            htable.put(key, entr.getKey(), entr.getValue());
        });
    }

    public static long hlen(String keyName) {
        return htable.row(keyName).keySet().size();
    }

    public static void sadd(String key, String value) {
        Object ifPresent = cache.getIfPresent(key);
        if (ifPresent == null) {
            cache.put(key, new LinkedHashSet<>());
        }
        Set ifPresent1 = (Set) cache.getIfPresent(key);
        ifPresent1.add(value);
    }

    public static void flushAll() {

    }

    public static void set(String key, String value) {
        cache.put(key, value);
    }

    public static void lpush(String key, String idstr) {
        Object ifPresent = cache.getIfPresent(key);
        if (ifPresent != null && ifPresent instanceof LinkedList) {
            ((LinkedList) ifPresent).push(idstr);
        } else {
            ifPresent = new LinkedList<>();
            ((LinkedList) ifPresent).push(idstr);
        }
        cache.put(key, ifPresent);
    }

    public static List lrange(String keyName, int from, int to) {

        Object ifPresent = cache.getIfPresent(keyName);
        if (ifPresent != null && ifPresent instanceof List) {
            if (from < 0) {
                from = ((List) ifPresent).size() + from;
            }
            if (to < 0) {
                to = ((List) ifPresent).size() + to;
            }
            return ((List) ifPresent).subList(from, to);
        } else {
            return new ArrayList();
        }
    }


    public enum KEYNAMES {
        ITEM_DATA("ItemData"),
        PET_FLAG("PetFlag"),
        SETITEM_DATA("SetItemInfo"),
        POTENTIAL_DATA("Potential"),
        SOCKET_DATA("Socket"),
        ITEM_NAME("ItemName"),
        ITEM_DESC("ItemDesc"),
        ITEM_MSG("ItemMsg"),
        HAIR_FACE_ID("HairFaces"),
        SKILL_DATA("SkillData"),
        SKILL_NAME("SkillName"),
        DELAYS("Delays"),
        SUMMON_SKILL_DATA("SummonSkillData"),
        MOUNT_ID("MountIDs"),
        FAMILIAR_DATA("Familiars"),
        CRAFT_DATA("Crafts"),
        SKILL_BY_JOB("SkillsByJob"),
        FINLA_ATTACK_SKILL("FinalAttackSkills"),
        MEMORYSKILL_DATA("MemorySkills"),
        //        DROP_DATA("DropData"),
//        DROP_DATA_GLOBAL("DropDataGlobal"),
//        DROP_DATA_SPECIAL("DropDataSpecial"),
        QUEST_COUNT("QuestCount"),
        NPC_NAME("NpcName"),
        PLAYER_DATA("PlayerData"),
        //        SHOP_DATA("ShopData"),
        MOBSKILL_DATA("MobSkillData"),
        MOB_NAME("MobName"),
        MOB_ID("MobIDs"),
        MAP_NAME("MapName"),
        MAP_LINKNPC("MapLinkNPC");

        public static final boolean DELETECACHE = false;
        private final String key;

        KEYNAMES(String key) {
            this.key = key;
        }

        public String getKeyName() {
            return key;
        }
    }
}

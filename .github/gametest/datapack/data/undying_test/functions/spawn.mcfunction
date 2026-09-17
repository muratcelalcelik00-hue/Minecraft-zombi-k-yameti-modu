# ut_a: attribute and daylight subject, under open sky.
summon minecraft:zombie 0 -59 0 {Tags:["ut_a"],PersistenceRequired:1b}
# ut_b: sense subject. Far from ut_a, 30 blocks short of where the TNT goes off.
summon minecraft:zombie 100 -59 0 {Tags:["ut_b"],PersistenceRequired:1b}
# ut_v: infection subject. Far from both zombies so nothing bites it.
summon minecraft:villager -100 -59 0 {Tags:["ut_v"],PersistenceRequired:1b}
say UNDYING-TEST spawned

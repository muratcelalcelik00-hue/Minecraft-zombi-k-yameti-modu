# Nothing below means anything if the subjects are not actually there.
execute if entity @e[tag=ut_a] run say UNDYING-TEST INFO subject-a-present
execute unless entity @e[tag=ut_a] run say UNDYING-TEST FAIL subject-a-missing
execute if entity @e[tag=ut_b] run say UNDYING-TEST INFO subject-b-present
execute unless entity @e[tag=ut_b] run say UNDYING-TEST FAIL subject-b-missing

# Did each summon command itself report success?
execute if score #sv ut matches 1 run say UNDYING-TEST INFO villager-summon-returned-ok
execute unless score #sv ut matches 1 run say UNDYING-TEST INFO villager-summon-returned-failure
execute if score #sc ut matches 1 run say UNDYING-TEST INFO cow-summon-returned-ok
execute unless score #sc ut matches 1 run say UNDYING-TEST INFO cow-summon-returned-failure

# And is anything actually standing there 20 ticks later?
execute if entity @e[tag=ut_v] run say UNDYING-TEST INFO villager-present
execute unless entity @e[tag=ut_v] run say UNDYING-TEST INFO villager-missing
execute if entity @e[tag=ut_c] run say UNDYING-TEST INFO cow-present
execute unless entity @e[tag=ut_c] run say UNDYING-TEST INFO cow-missing

# Untagged count, in case the entity exists but the tag did not stick.
execute store result score #vc ut if entity @e[type=minecraft:villager]
execute if score #vc ut matches 1.. run say UNDYING-TEST INFO villagers-exist-somewhere
execute if score #vc ut matches 0 run say UNDYING-TEST INFO no-villagers-in-world

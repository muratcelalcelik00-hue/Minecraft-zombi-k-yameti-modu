execute if entity @e[tag=ut_v,nbt={ActiveEffects:[{}]}] run say UNDYING-TEST PASS infection-applied
execute unless entity @e[tag=ut_v,nbt={ActiveEffects:[{}]}] run say UNDYING-TEST FAIL infection-applied

# 140 ticks in, the health ceiling has dropped a little below the villager's 20.
execute store result score #vhp ut run data get entity @e[tag=ut_v,limit=1] Health 100
execute if score #vhp ut matches 1500..1999 run say UNDYING-TEST PASS infection-drains-health
execute unless score #vhp ut matches 1500..1999 run say UNDYING-TEST FAIL infection-drains-health

execute if score #vhp ut matches ..0 run say UNDYING-TEST INFO villager-health-unreadable
execute if score #vhp ut matches 2000.. run say UNDYING-TEST INFO villager-health-full

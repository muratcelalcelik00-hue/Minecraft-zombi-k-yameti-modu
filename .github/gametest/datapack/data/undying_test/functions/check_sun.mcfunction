# 540 ticks of noon under open sky. An unprotected zombie is dead well before this.
execute if entity @e[tag=ut_a] run say UNDYING-TEST PASS alive-in-daylight
execute unless entity @e[tag=ut_a] run say UNDYING-TEST FAIL alive-in-daylight

execute store result score #hp ut run data get entity @e[tag=ut_a,limit=1] Health 100
execute if score #hp ut matches 1800..2000 run say UNDYING-TEST PASS no-sun-damage
execute unless score #hp ut matches 1800..2000 run say UNDYING-TEST FAIL no-sun-damage

# Not an assertion: tells us whether the zombie merely survives the fire or is
# never lit in the first place. Fire is -20 on an entity that is not burning.
execute store result score #fire ut run data get entity @e[tag=ut_a,limit=1] Fire
execute if score #fire ut matches ..0 run say UNDYING-TEST INFO fire-ticks-cleared
execute unless score #fire ut matches ..0 run say UNDYING-TEST INFO fire-ticks-still-set

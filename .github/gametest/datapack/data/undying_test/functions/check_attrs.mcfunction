# Vanilla zombie bases: speed 0.23, attack 3.0, follow range 35.0, knockback 0.0.
# Configured multipliers: 0.92, 1.25, 1.35, and a flat +0.3.
execute store result score #spd ut run attribute @e[tag=ut_a,limit=1] minecraft:generic.movement_speed get 100000
execute if score #spd ut matches 21100..21220 run say UNDYING-TEST PASS speed-modifier
execute unless score #spd ut matches 21100..21220 run say UNDYING-TEST FAIL speed-modifier

# The regression test for the compounding bug: the base value must be untouched,
# because everything is applied as a transient modifier on top of it.
execute store result score #base ut run attribute @e[tag=ut_a,limit=1] minecraft:generic.movement_speed base get 100000
execute if score #base ut matches 22900..23100 run say UNDYING-TEST PASS base-value-untouched
execute unless score #base ut matches 22900..23100 run say UNDYING-TEST FAIL base-value-untouched

execute store result score #atk ut run attribute @e[tag=ut_a,limit=1] minecraft:generic.attack_damage get 1000
execute if score #atk ut matches 3700..3800 run say UNDYING-TEST PASS attack-modifier
execute unless score #atk ut matches 3700..3800 run say UNDYING-TEST FAIL attack-modifier

execute store result score #fr ut run attribute @e[tag=ut_a,limit=1] minecraft:generic.follow_range get 1000
execute if score #fr ut matches 47000..47500 run say UNDYING-TEST PASS follow-range-modifier
execute unless score #fr ut matches 47000..47500 run say UNDYING-TEST FAIL follow-range-modifier

execute store result score #kb ut run attribute @e[tag=ut_a,limit=1] minecraft:generic.knockback_resistance get 1000
execute if score #kb ut matches 250..360 run say UNDYING-TEST PASS knockback-modifier
execute unless score #kb ut matches 250..360 run say UNDYING-TEST FAIL knockback-modifier

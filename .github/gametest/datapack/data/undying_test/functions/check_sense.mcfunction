# ut_b started at x=100 with no target and nothing else to do. If it moved east,
# it did so because it heard the explosion.
execute store result score #bx ut run data get entity @e[tag=ut_b,limit=1] Pos[0] 1
execute if score #bx ut matches 106.. run say UNDYING-TEST PASS sense-investigate
execute unless score #bx ut matches 106.. run say UNDYING-TEST FAIL sense-investigate

execute if score #bx ut matches ..100 run say UNDYING-TEST INFO sense-x-le-100
execute if score #bx ut matches 101..105 run say UNDYING-TEST INFO sense-x-101-105
execute if score #bx ut matches 106..120 run say UNDYING-TEST INFO sense-x-106-120
execute if score #bx ut matches 121.. run say UNDYING-TEST INFO sense-x-ge-121

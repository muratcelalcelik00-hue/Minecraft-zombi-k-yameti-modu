# 300 seconds is the configured infectionDurationTicks (6000) exactly.
# store success tells us whether the command found a target and applied it,
# which a bare say cannot.
execute store success score #eok ut run effect give @e[tag=ut_v,limit=1] undying:infection 300 0 true
execute if score #eok ut matches 1 run say UNDYING-TEST INFO effect-command-applied
execute unless score #eok ut matches 1 run say UNDYING-TEST INFO effect-command-did-nothing
execute if entity @e[tag=ut_v] run say UNDYING-TEST INFO villager-present-at-infect
execute unless entity @e[tag=ut_v] run say UNDYING-TEST INFO villager-gone-at-infect

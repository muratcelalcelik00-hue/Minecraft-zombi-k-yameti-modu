# 300 seconds is the configured infectionDurationTicks (6000) exactly.
# store success records whether the command found its target, which a bare say
# cannot — a silent no-op here once looked like a broken mod.
execute store success score #eok ut run effect give @e[tag=ut_v,limit=1] undying:infection 300 0 true
execute if score #eok ut matches 1 run say UNDYING-TEST INFO effect-command-applied
execute unless score #eok ut matches 1 run say UNDYING-TEST INFO effect-command-did-nothing

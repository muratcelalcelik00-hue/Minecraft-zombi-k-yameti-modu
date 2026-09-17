# Nothing below means anything if the subjects are not actually there, and a
# missing subject reads exactly like a passing assertion further down: the score
# is never written, so it keeps whatever it had. This is the guard.
execute if entity @e[tag=ut_a] run say UNDYING-TEST PASS subject-a-present
execute unless entity @e[tag=ut_a] run say UNDYING-TEST FAIL subject-a-missing
execute if entity @e[tag=ut_b] run say UNDYING-TEST PASS subject-b-present
execute unless entity @e[tag=ut_b] run say UNDYING-TEST FAIL subject-b-missing
execute if entity @e[tag=ut_v] run say UNDYING-TEST PASS subject-v-present
execute unless entity @e[tag=ut_v] run say UNDYING-TEST FAIL subject-v-missing

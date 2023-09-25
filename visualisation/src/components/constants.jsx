export const modelPlaceholder =
    '# Model = (S, I, T, F) # Type \'this\' to use this model or \'compact\' for compact\n' +
    'S = {s1> [!p !q], s2 [!p q],\n' +
    '     s3 [p !q], s4< [p q \'deadlock\']}            # Set of states\n' +
    'I = {s1 [\'starting here\']}                       # Set of initial states\n' +
    'T = {(s1, s2), (s2, s1), (s1, s3), (s3, s1),\n' +
    '     (s3, s4) [\'unsafe transition\'], (s4, s1)}   # Set of transitions (s, s\')\n' +
    'F = {}                         # Set of final states (you can omit empty sets)\n' +
    '# For boolean encoding use \'>\' as suffix for start-, and \'<\' for goal states'

export const compactModelPlaceholder =
    '# Type \'compact\' to use this model\n' +
    '# Initial states are denoted by \'_\' as suffix, final states by \'*\'\n' +
    '# For boolean encoding use \'>\' as suffix for start-, and \'<\' for goal states\n' +
    '# Both states and transitions can be labeled with \'[\'Text: \' var1 var2]\'\n' +
    '# Transitions are denoted by either \'->\' for unidirectional transitions\n' +
    '# or \'-\' for bidirectional transitions\n' +
    's1_> [!p !q] - [\'bidirectional\'] s2 [!p q], s1 - s3 [p !q],\n' +
    's3 -> [\'unsafe transition\'] s4< [p q \'deadlock\'], s4 -> s1'

export const cleanResultData = (data) => data.replaceAll(/[$]/g, '\n')

export const serverURL = 'http://localhost:4000'

export const algorithmURL = 'http://localhost:5000'

export const solutionInfoWarning =
    'Keeping a lot of solution information may lag your page!\n' +
    'If that happens, press F5 to refresh the page.\n' +
    'Your FORMULA, SOLUTION and MODEL will persist through the refresh!'
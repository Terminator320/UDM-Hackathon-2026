def check_all_substrings(big_string, small_strings):
    # Returns True only if every sub in small_strings is found in big_string
    return all(sub in big_string for sub in small_strings)

def verify(big_string, small_strings):
    if big_string == None :
        print("No string...")
        return
    
    if check_all_substrings(big_string, small_strings):
        print("All substrings were found!")
        print("Length : ", len(big_string))
    else:
        print("One or more substrings are missing.")

def read_frags(filename):
    frags = []
    with open(filename, "r",encoding="utf-8") as f:
        for line in f:
            line = line.replace("\n", "")
            if not line:
                continue
            if len(line) == 0:
                continue
            frags.append(line)
    return frags

def initialize():
    frags = read_frags("input_level_5.txt") # input_demo input_level_1 input_level_2 input_level_3 input_level_4
    return frags

def reverse_shreddify(frags):
    frags = list(set(frags))
    new_lst = []
    for x in frags:
        has_it = False
        for n in frags:
            if x != n and x in n:
                has_it = True
        if not has_it:
            new_lst.append(x)

    frags = new_lst
        
    while len(frags) > 1:
        max_overlap = -1
        best_i, best_j = None, None
        merged = ""

        for i in range(len(frags)):
            for j in range(len(frags)):
                if i == j:
                    continue

                a = frags[i]
                b = frags[j]

                max_len = min(len(a), len(b))
                for k in range(1, max_len + 1):
                    if a[-k:] == b[:k]:
                        if k > max_overlap:
                            max_overlap = k
                            best_i, best_j = i, j
                            merged = a + b[k:]

        if max_overlap == -1:
            frags[0] += frags.pop()
        else:
            new_frags = []
            for idx in range(len(frags)):
                if idx != best_i and idx != best_j:
                    new_frags.append(frags[idx])
            new_frags.append(merged)
            frags = new_frags

    return frags[0]

if __name__ == "__main__":
    frags = initialize()

    solution_naive = "".join(frags)
    print(solution_naive)
    verify(solution_naive, frags)

    good_solution = reverse_shreddify(frags)
    # Pour input_demo.txt la meilleure réponse (la plus courte string) est de longeur 53
    #good_solution = "Demain,_dèsl’aube,à_l’heure_où_blanchit_la_campagne"
    print(good_solution)
    verify(good_solution, frags)

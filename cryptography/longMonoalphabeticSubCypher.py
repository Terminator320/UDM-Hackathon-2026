import random
import string
import math

ALPHABET = string.ascii_lowercase

# -----------------------------
# Load quadgrams
# -----------------------------
def load_quadgrams(filename="quadgrams.txt"):
    quadgrams = {}
    total = 0
    with open(filename) as f:
        for line in f:
            quad, count = line.split()
            count = int(count)
            quadgrams[quad.lower()] = count
            total += count

    # convert counts to log probabilities
    for q in quadgrams:
        quadgrams[q] = math.log10(quadgrams[q] / total)

    floor = math.log10(0.01 / total)
    return quadgrams, floor

# -----------------------------
# Load dictionary
# -----------------------------
def load_dictionary(filename="words.txt"):
    words = set()
    with open(filename) as f:
        for w in f:
            w = w.strip().lower()
            if w.isalpha():
                words.add(w)
    return words

# -----------------------------
# Score text with quadgrams + dictionary
# -----------------------------
def score_text(text, quadgrams, quad_floor, dictionary, dict_weight=1.0):
    # Quadgram score
    score = 0.0
    cleaned = ''.join(c for c in text.lower() if c.isalpha())
    for i in range(len(cleaned) - 3):
        quad = cleaned[i:i+4]
        score += quadgrams.get(quad, quad_floor)

    # Dictionary score
    words = text.lower().split()
    dict_score = sum(len(w) for w in words if w in dictionary)
    score += dict_weight * dict_score
    return score

# -----------------------------
# Apply key
# -----------------------------
def decrypt(text, key):
    table = str.maketrans(ALPHABET, key)
    return text.translate(table)

# -----------------------------
# Random key
# -----------------------------
def random_key():
    k = list(ALPHABET)
    random.shuffle(k)
    return ''.join(k)

# -----------------------------
# Hill climbing solver
# -----------------------------
def solve(ciphertext, quadgrams, quad_floor, dictionary,
          restarts=10, iterations=5000, dict_weight=1.0):

    best_global_score = float('-inf')
    best_global_plain = ""

    for _ in range(restarts):
        key = random_key()
        plaintext = decrypt(ciphertext, key)
        best_score = score_text(plaintext, quadgrams, quad_floor, dictionary, dict_weight)

        for _ in range(iterations):
            # swap two letters
            a, b = random.sample(range(26), 2)
            key_list = list(key)
            key_list[a], key_list[b] = key_list[b], key_list[a]
            new_key = ''.join(key_list)

            new_plain = decrypt(ciphertext, new_key)
            new_score = score_text(new_plain, quadgrams, quad_floor, dictionary, dict_weight)

            if new_score > best_score:
                key = new_key
                best_score = new_score
                plaintext = new_plain

        if best_score > best_global_score:
            best_global_score = best_score
            best_global_plain = plaintext

    return best_global_plain

# -----------------------------
# Run
# -----------------------------
if __name__ == "__main__":
    # Load resources
    quadgrams, quad_floor = load_quadgrams("quadgrams.txt")
    dictionary = load_dictionary("words.txt")

    # Replace this with your ciphertext
    ciphertext = """
    omkhhz wfhylsqkp wv bys qcdphz ft wqdsv tvhwmig rn kvh icdkissw igdhii cq imsuixs lv nflbksq trbjcruh. tvhwmi tumhihvtm dvrzbazg giksv jrqn bf hkm rfdj dowpvadbzqlie oo-szbgq (t. og 801-873), eyc iwiadtcm gmmsowgsg bys pmkvrl kc ezvon kzdkmig. omkhhz wfhylsqkp oqicmvqj udqesg qddrzkoqkv wq mlfrxv klby hkm usymccsuvbw ww ardrpom kmsm zb dl 1450, nvhzvwq wes pcjh hakwpiks wpv opwlbw ww hbxv fhylwumu trz vofp cswbvfiwia. oqeuxqjhv cjs omkhhz wfhylsqkp oqicmvqj ov i iigqdsqbrfb bvqkvzexm wcu trbjcruh qusqbztlkrhlwe, kkmis lb zg siihlklzdzcm hnwsfbzjh ij oq qerlkrhlwe ci eyswpvf dv lbnvfkq eiwwqeu vgjhhu zg dtgvdjvhlk, jmotrplk, ff llvcjzrdkqt.

hkm lgh ww zhbksu nistcvbfqvg dvu tumhihvtm dvrzbazg strmv i wiqlrahvkoo zfzh qe qugghroiopa rbg avjhzrz zwir scqnom xopmj, wqkcigqeu kieupie, gfzrpetv, krzuzh ier wpv hhtvjlazcq orah aycz eysht ft iwihxvv. cqm ft wpv sdzcwhak rhatflxkwrvj wq kcovazqdt cwwmiowcis rn rdstpwqo kvh secztvrjm ft hvxzlay zhbksu nistcvbfg kc vwcjlvx o fzpdwwxfdu zg iwlbg qe sgorf dtcoq xfs'v nrarcj gwwim "wpv urtu-pxo", nvhzv hkm dswpfr la jifkvgvnlzog rdstzsg bf rhkzdkmi o pmjgdov uldzbj bys owtowqfb rn r humrgxzv vllusq jp qdxkolv bwgl.

ysujvfw a. qwp, qe vla tzdajwf qehuwuifbffb kimsbfuuigvb bvlw kfrha rbg avqumk kuqkwqo, xwymj hkm vbjtzgk tvhwmi tumhihvtm vmhihvts da "vhdwe flayr ontax opdzj mymfqe", wpv arak qrudcq tvhwmi ddqig da "kv km rb um vf lv fb db er vb vg hv ft wm vr rz kw kq rg ww", rbg bys pwjh fwdarv ucxjcsg tvhwmig da "cz hm jg rw kh in if qv gd fk". uwinvfhvk kdgj ci kfiqbzbj krb szfrxkv gruvkkik rlnwsumeh rzusua.

cswbvf izvexmeqlmj ooaf vddv o vbicqo vtimth rv kvh lvgloe ci afah svmewrfg trmrckg. wpv arak tumhihvk zhbksua rfh xcofmu cq bys kwds uwn ci bys etzqnmeggmithz kmsmnflbvf, wpv rywion svmewrfg trmrck, qrtvads rbg wkvhz fdwqdwcmu zdgfiwa. 

sfddf jrcj oymq fhafzxb cs gmiblmi bldvox lv rlnwwfcks.
    """

    # Solve
    plaintext = solve(ciphertext.lower(), quadgrams, quad_floor, dictionary,
                      restarts=20, iterations=20000, dict_weight=1.0)

    print("\nDECRYPTED TEXT:\n")
    print(plaintext)

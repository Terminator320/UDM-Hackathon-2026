import string
from collections import Counter
import random

# -----------------------------
# Load dictionary (lowercase)
# -----------------------------
def load_dictionary(filename="words.txt"):
    with open(filename) as f:
        words = [w.strip().lower() for w in f if w.strip().isalpha()]
    return set(words)

# -----------------------------
# Apply a mapping to text (lowercase)
# -----------------------------
def apply_mapping(ciphertext, mapping):
    plaintext = ""
    for c in ciphertext:
        if c.lower() in mapping:
            plaintext += mapping[c.lower()]
        else:
            plaintext += c
    return plaintext

# -----------------------------
# Score plaintext by valid words
# Longer words get more weight
# -----------------------------
def score_plaintext(plaintext, dictionary):
    words = [w.lower() for w in plaintext.split()]
    valid_score = sum(len(w) for w in words if w in dictionary)
    return valid_score

# -----------------------------
# Initial frequency-based mapping
# -----------------------------
ENGLISH_FREQ_ORDER = "etaoinshrdlcumwfgypbvkjxqz"

def initial_mapping(ciphertext):
    text = ''.join(c for c in ciphertext if c.isalpha()).lower()
    count = Counter(text)
    sorted_letters = [item[0] for item in count.most_common()]
    mapping = {}
    for c, e in zip(sorted_letters, ENGLISH_FREQ_ORDER):
        mapping[c] = e
    return mapping

# -----------------------------
# Iterative refinement (greedy swap)
# -----------------------------
def refine_mapping(ciphertext, dictionary, mapping, iterations=2000):
    best_mapping = mapping.copy()
    best_score = score_plaintext(apply_mapping(ciphertext, best_mapping), dictionary)
    letters = list(string.ascii_lowercase)

    for _ in range(iterations):
        a, b = random.sample(letters, 2)
        temp_mapping = best_mapping.copy()
        # Swap letters in mapping
        for k, v in temp_mapping.items():
            if v == a:
                temp_mapping[k] = b
            elif v == b:
                temp_mapping[k] = a

        # Score the new mapping
        plaintext_candidate = apply_mapping(ciphertext, temp_mapping)
        candidate_score = score_plaintext(plaintext_candidate, dictionary)

        if candidate_score > best_score:
            best_mapping = temp_mapping
            best_score = candidate_score

    return best_mapping

# -----------------------------
# Solver for large cryptograms
# -----------------------------
def solve_large_cipher(ciphertext, dictionary_file="words.txt", attempts=5):
    dictionary = load_dictionary(dictionary_file)
    best_score = 0
    best_plaintext = ""

    for _ in range(attempts):
        mapping = initial_mapping(ciphertext)
        mapping = refine_mapping(ciphertext, dictionary, mapping, iterations=5000)
        pt = apply_mapping(ciphertext, mapping)
        s = score_plaintext(pt, dictionary)
        if s > best_score:
            best_score = s
            best_plaintext = pt

    return best_plaintext

# -----------------------------
# Example usage
# -----------------------------
if __name__ == "__main__":
    ciphertext = """
    	njuuax.

    rhgnc mta pwgla jcjgn! ta hbrnc tgh jmmanmgwn yjlz mw tai qjla.

    "g twex jm fwri nalz mta cwk ojyyji," hta hjgx. "mta cwk ojyyji, mta tgct-tjnxax anakf. gm'h j naaxea bgmt j xiwu wq uwghwn wn gmh mgu. jt-jt! xwn'm uree jbjf wi fwr'ee qaae mtjm uwghwn."

    ujre migax mw hbjeewb gn j xif mtiwjm. ta lwrex nwm mjza tgh jmmanmgwn qiwk mta hajkax wex qjla, mta ceghmangnc afah, mta ujea crkh jiwrnx hgepaif kamje maamt mtjm qejhtax jh hta huwza.

    "j xrza'h hwn krhm znwb jywrm uwghwnh," hta hjgx. "gm'h mta bjf wq wri mgkah, at? krhzf, mw ya uwghwnax gn fwri xignz. jrkjh, mw ya uwghwnax gn fwri qwwx. mta drglz wnah jnx mta hewb wnah jnx mta wnah gn yambaan. taia'h j nab wna qwi fwr: mta cwk ojyyji. gm zgeeh wnef jngkjeh."

    uigxa wpailjka ujre'h qaji. "fwr xjia hrccahm j xrza'h hwn gh jn jngkje?" ta xakjnxax.

    "eam rh hjf g hrccahm fwr kjf ya trkjn," hta hjgx. "hmajxf! g bjin fwr nwm mw mif oaizgnc jbjf. g jk wex, yrm kf tjnx ljn xigpa mtgh naaxea gnmw fwri nalz yaqwia fwr ahljua ka."

    "btw jia fwr?" ta btghuaiax. "twb xgx fwr miglz kf kwmtai gnmw eajpgnc ka jewna bgmt fwr? jia fwr qiwk mta tjizwnnanh?"

    "mta tjizwnnanh? yeahh rh, nw! nwb, ya hgeanm." j xif qgncai mwrltax tgh nalz jnx ta hmgeeax mta gnpwernmjif rica mw eaju jbjf.

    "cwwx," hta hjgx. "fwr ujhh mta qgihm mahm. nwb, taia'h mta bjf wq mta iahm wq gm: gq fwr bgmtxijb fwri tjnx qiwk mta yws fwr xga. mtgh gh mta wnef irea. zaau fwri tjnx gn mta yws jnx egpa. bgmtxijb gm jnx xga."

    ujre mwwz j xaau yiajmt mw hmgee tgh miakyegnc. "gq g ljee wrm mtaia'ee ya haipjnmh wn fwr gn halwnxh jnx fwr'ee xga."

    "haipjnmh bgee nwm ujhh fwri kwmtai btw hmjnxh crjix wrmhgxa mtjm xwwi. xauanx wn gm. fwri kwmtai hripgpax mtgh mahm. nwb gm'h fwri mrin. ya twnwiax. ba haexwk jxkgnghmai mtgh mw kan-ltgexian."     

    lrigwhgmf iaxrlax ujre'h qaji mw j kjnjcajyea eapae. ta tajix mirmt gn mta wex bwkjn'h pwgla, nw xanfgnc gm. gq tgh kwmtai hmwwx crjix wrm mtaia ... gq mtgh baia miref j mahm.... jnx btjmapai gm bjh, ta znab tgkhaeq ljrctm gn gm, mijuuax yf mtjm tjnx jm tgh nalz: mta cwk ojyyji. ta ialjeeax mta iahuwnha qiwk mta egmjnf jcjgnhm qaji jh tgh kwmtai tjx mjrctm tgk wrm wq mta yana cahhaigm igma.       

    "g krhm nwm qaji. qaji gh mta kgnx-zgeeai. qaji gh mta egmmea-xajmt mtjm yignch mwmje wyegmaijmgwn. g bgee qjla kf qaji. g bgee uaikgm gm mw ujhh wpai ka jnx mtiwrct ka. jnx btan gm tjh cwna ujhm g bgee mrin mta gnnai afa mw haa gmh ujmt. btaia mta qaji tjh cwna mtaia bgee ya n
    """
    plaintext = solve_large_cipher(ciphertext, dictionary_file="words.txt", attempts=5)
    print("Most likely plaintext:\n")
    print(plaintext)

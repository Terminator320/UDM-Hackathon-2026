import argparse
import math
import re
import unicodedata

ALPHABET = "abcdefghijklmnopqrstuvwxyz"
ALPHABET_UPPER = ALPHABET.upper()

LETTER_FREQ = {
    "en": {
        "a": 8.17,
        "b": 1.49,
        "c": 2.78,
        "d": 4.25,
        "e": 12.70,
        "f": 2.23,
        "g": 2.02,
        "h": 6.09,
        "i": 6.97,
        "j": 0.15,
        "k": 0.77,
        "l": 4.03,
        "m": 2.41,
        "n": 6.75,
        "o": 7.51,
        "p": 1.93,
        "q": 0.10,
        "r": 5.99,
        "s": 6.33,
        "t": 9.06,
        "u": 2.76,
        "v": 0.98,
        "w": 2.36,
        "x": 0.15,
        "y": 1.97,
        "z": 0.07,
    },
    "fr": {
        "a": 7.64,
        "b": 0.90,
        "c": 3.26,
        "d": 3.67,
        "e": 14.72,
        "f": 1.07,
        "g": 0.87,
        "h": 0.74,
        "i": 7.53,
        "j": 0.61,
        "k": 0.05,
        "l": 5.46,
        "m": 2.97,
        "n": 7.10,
        "o": 5.80,
        "p": 2.52,
        "q": 1.36,
        "r": 6.69,
        "s": 7.95,
        "t": 7.24,
        "u": 6.31,
        "v": 1.83,
        "w": 0.04,
        "x": 0.43,
        "y": 0.13,
        "z": 0.33,
    },
}

SMALL_WORDS_EN = {
    "the",
    "be",
    "to",
    "of",
    "and",
    "a",
    "in",
    "that",
    "have",
    "i",
    "it",
    "for",
    "not",
    "on",
    "with",
    "he",
    "as",
    "you",
    "do",
    "at",
    "this",
    "but",
    "his",
    "by",
    "from",
    "they",
    "we",
    "say",
    "her",
    "she",
    "or",
    "an",
    "will",
    "my",
    "one",
    "all",
    "would",
    "there",
    "their",
    "what",
    "so",
    "up",
    "out",
    "if",
    "about",
    "who",
    "get",
    "which",
    "go",
    "me",
}

SMALL_WORDS_FR = {
    "le",
    "de",
    "un",
    "etre",
    "et",
    "a",
    "il",
    "avoir",
    "ne",
    "je",
    "son",
    "que",
    "se",
    "qui",
    "ce",
    "dans",
    "en",
    "du",
    "elle",
    "au",
    "deux",
    "mais",
    "nous",
    "vous",
    "comme",
    "ou",
    "sur",
    "mon",
    "me",
    "leur",
    "y",
    "dire",
    "bien",
    "sans",
    "oui",
    "non",
    "plus",
    "peu",
    "tres",
    "tout",
    "fait",
    "par",
    "pour",
    "pas",
    "quoi",
    "quand",
    "comment",
    "avec",
}

WORD_RE = re.compile(r"[A-Za-zÀ-ÿ]+")


def strip_accents(text: str) -> str:
    normalized = unicodedata.normalize("NFKD", text)
    return "".join(c for c in normalized if not unicodedata.combining(c))


def normalize_word(word: str) -> str:
    return strip_accents(word.lower())


def build_log_freq(lang: str) -> dict[str, float]:
    base = LETTER_FREQ.get(lang, LETTER_FREQ["en"])
    total = sum(base.values())
    return {ch: math.log(base[ch] / total) for ch in ALPHABET}


def load_wordset(lang: str) -> set[str]:
    if lang == "fr":
        return set(SMALL_WORDS_FR)
    return set(SMALL_WORDS_EN)


def build_caesar_tables() -> list[dict[int, int]]:
    tables = []
    for shift in range(26):
        lower = ALPHABET[shift:] + ALPHABET[:shift]
        upper = ALPHABET_UPPER[shift:] + ALPHABET_UPPER[:shift]
        trans = str.maketrans(lower + upper, ALPHABET + ALPHABET_UPPER)
        tables.append(trans)
    return tables


CAESAR_TABLES = build_caesar_tables()


def caesar_shift(text: str, shift: int) -> str:
    return text.translate(CAESAR_TABLES[shift % 26])


def score_text(text: str, log_freq: dict[str, float], wordset: set[str]) -> float:
    score = 0.0
    for ch in text.lower():
        if ch in log_freq:
            score += log_freq[ch]

    words = WORD_RE.findall(text)
    for w in words:
        nw = normalize_word(w)
        if not nw:
            continue
        if nw in wordset:
            score += 2.0 + 0.2 * min(len(nw), 10)
        else:
            score -= 0.1 * min(len(nw), 12)
    return score


def score_for_lang(ciphertext: str, lang: str) -> tuple[float, str, int]:
    log_freq = build_log_freq(lang)
    wordset = load_wordset(lang)

    best_score = -1e30
    best_shift = 0
    best_plain = ciphertext

    for shift in range(26):
        plain = caesar_shift(ciphertext, shift)
        score = score_text(plain, log_freq, wordset)
        if score > best_score:
            best_score = score
            best_shift = shift
            best_plain = plain

    return best_score, best_plain, best_shift


def solve_caesar(ciphertext: str, lang: str | None) -> tuple[str, int, str]:
    if lang:
        _, plain, shift = score_for_lang(ciphertext, lang)
        return plain, shift, lang

    best = (-1e30, ciphertext, 0, "en")
    for candidate in ("en", "fr"):
        score, plain, shift = score_for_lang(ciphertext, candidate)
        if score > best[0]:
            best = (score, plain, shift, candidate)

    return best[1], best[2], best[3]


def main() -> None:
    parser = argparse.ArgumentParser(description="Solve Caesar cipher for English or French.")
    parser.add_argument("text", nargs="?", help="Ciphertext string (if omitted, prompts)")
    parser.add_argument("--lang", choices=["en", "fr"], help="Language for scoring (auto if omitted)")
    args = parser.parse_args()

    if args.text:
        ciphertext = args.text
    else:
        ciphertext = input("Enter ciphertext: ")

    plaintext, shift, lang = solve_caesar(ciphertext, args.lang)

    print(plaintext)
    print(f"\nshift: {shift}")
    print(f"language: {lang}")


if __name__ == "__main__":
    main()

import argparse


def rotate_word_decode(text: str) -> str:
    words = text.split()
    decoded = []
    for w in words:
        if len(w) <= 1:
            decoded.append(w)
        else:
            decoded.append(w[-1] + w[:-1])
    return " ".join(decoded)


def main() -> None:
    parser = argparse.ArgumentParser(description="Decode words with last-letter-to-front rotation.")
    parser.add_argument("text", nargs="?", help="Ciphertext string (if omitted, prompts)")
    args = parser.parse_args()

    if args.text:
        ciphertext = args.text
    else:
        ciphertext = input("Enter ciphertext: ")

    plaintext = rotate_word_decode(ciphertext)
    print(plaintext)


if __name__ == "__main__":
    main()
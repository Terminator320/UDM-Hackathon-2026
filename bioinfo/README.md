#### Assurez-vous d'avoir installé python, et pip pour installer les librairies requises (utiliser un venv est facultatif, mais recommandé pour ne pas perturber vos autres défis)

## Défi 1 Décodeur de génome

### Objectif

Vous surveillez un flux de génome ARN en temps réel sur un vieil ordinateur. Les bases arrivent une par une : **impossible de tout stocker ou d’attendre la fin**

Construire un **décodeur de génome ARN** capable de :
- lire une séquence **base par base**,
- former des **codons**,
- détecter des **gènes** (START -> STOP),
- produire des **événements** en temps réel,
- afficher l’évolution de la lecture.

---

### Entrée donnée

Un flux d’ARN vous est transmis pour analyse, mais comme vous avez un "ordi"nosaure, vous devez lire cet ARN une base à la fois...
---

### Contraintes fondamentales /!\

- La lecture est **strictement séquentielle**
- Aucun accès direct à la séquence complète
- Les codons doivent être formés **au fur et à mesure**
- Un gène est défini comme :
  - un `START`,
  - suivi de codons,
  - terminé par un `STOP`

---

### Ce qui est attendu

- Détection correcte des codons
- Détection du nombre correct de gènes construits
- Gestion des cas :
  - bases invalides,
  - codons inconnus,
  - gènes incomplets
- Affichage en temps réel :
  - ARN entrant,
  - événements récents,
  - gène en cours,
  - statistiques


---

## Défi 2 Introns et exons (épissage)

### Objectif
Le contenu d’un ARN n’est pas traduit à 100 %. **Certaines portions de la séquence, appelées introns, sont supprimées avant la traduction**. 
Le gène effectivement exprimé diffère donc de la séquence originale, ce qui complique toute tentative de reverse engineering à partir du gène final observé (tant mieux?)

VOus devez simuler le **processus d’épissage** vous même:
- identifier des **introns** dans une séquence ARN,
- produire un **ARN mature**,
- traduire uniquement les **exons** en gènes

---

### !!! RECAP biologie !!!

- Un intron commence par les acides aminés `GU`
- Un intron se termine par `AG`
- Tout ce qui est **entre GU et AG** est supprimé (intron)
- La lecture continue **base par base**

---

### Contraintes

- Détection **en streaming** (pas de traitement via retours arrières, pas de relecture autorisée !!)

---

### Ce qui est attendu

- Détection correcte des introns
- Suppression correcte des introns
- Construction de l’ARN mature
- Traduction correcte des exons
- Détection des gènes (START --> STOP)
- Visualisation (cette partie est donnée, codez la logique métier uniquement) :
  - ARN entrant,
  - événements (INTRON_START, INTRON_END, CODON, START, STOP),
  - ARN mature,
  - statistiques

---



## Défi 3 - Multi-Frames Decoder

### Objectif

Sachant lire et épisser un gène, il serait intéressant maintenant de lire une séquence à partir d'un différent point de départ et comparer les gène potentiels
Analysez une séquence ARN selon **les 3 cadres de lecture possibles** (frames 0, 1, 2) et déterminez **à quel point on gagne ou perd en matériel génétique lorsqu'on démarre la lecture de facon décalée**

---

### Principe

- Chaque frame possède :
  - un **offset initial**,
  - son propre buffer,
  - ses propres gènes,
  - son propre score
- Tous lisent **exactement la même séquence**, base par base.

---

### Contraintes importantes

- Les frames ne choisissent pas quand commencer :
  - le décalage est **structurel**
- Un frame ne doit **ignorer que les premières bases**


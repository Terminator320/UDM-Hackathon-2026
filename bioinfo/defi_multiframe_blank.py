import time
from rich.console import Console
from rich.panel import Panel
from rich.live import Live
from rich.columns import Columns
from rich.layout import Layout
from rich.table import Table


CODON_TABLE = {
    "AUG": "START",
    "ACG": "START",
    "AAA": "LYS",
    "AAC": "ASN",
    "AAU": "ASN",
    "GGU": "GLY",
    "GGC": "GLY",
    "GGA": "GLY",
    "GGG": "GLY",
    "UUU": "PHE",
    "UUC": "PHE",
    "CCC": "PRO",
    "CCU": "PRO",
    "UAA": "STOP",
    "UAG": "STOP",
    "UGA": "STOP",
}


class FrameDecoder:
    def __init__(self, frame_id: int):
        self.frame_id = frame_id
        self.offset = frame_id
        self.base_count = 0
        self.codon_count = 0
        self.all_genes = []
        self._bases_skipped = 0
        self._codon_buffer = []
        self._in_gene = False
        self._current_gene = []

    def process_base(self, base: str):
        events = []

        if self._bases_skipped < self.offset:
            self._bases_skipped += 1
            return events

        b = base.upper()
        if b not in ("A", "U", "G", "C"):
            return events

        self.base_count += 1
        self._codon_buffer.append(b)
        if len(self._codon_buffer) < 3:
            return events

        codon = "".join(self._codon_buffer)
        self._codon_buffer.clear()
        self.codon_count += 1

        token = CODON_TABLE.get(codon, "UNKNOWN")
        events.append(("CODON", codon, token))

        if token == "START":
            if not self._in_gene:
                self._in_gene = True
                self._current_gene = ["START"]
                events.append(("START",))
            else:
                self._current_gene.append("START")
        elif token == "STOP":
            if self._in_gene:
                self._current_gene.append("STOP")
                self.all_genes.append(list(self._current_gene))
                events.append(("STOP", list(self._current_gene)))
                self._in_gene = False
                self._current_gene = []
        else:
            if self._in_gene:
                self._current_gene.append(token)

        return events

    def get_score(self):
        score = 0
        for gene in self.all_genes:
            score += max(0, len(gene) - 2)
        return score

    def get_best_gene(self):
        if not self.all_genes:
            return None
        
        return max(self.all_genes, key=len)


class MultiFrameDecoder:
    def __init__(self):
        self.frames = [
            FrameDecoder(0),
            FrameDecoder(1),
            FrameDecoder(2),
        ]

    def process_base(self, base: str):
        """
        Distribue la base à tous les frames
        """
        for frame in self.frames:
            frame.process_base(base)

    def get_best_frame(self):
        """
        Retourne l'indice du frame considéré comme le meilleur selon vous ;)
        """
        best_id = 0
        best_score = self.frames[0].get_score()
        for i, frame in enumerate(self.frames[1:], start=1):
            score = frame.get_score()
            if score > best_score:
                best_score = score
                best_id = i
        return best_id


# AFFICHAGE CONSOLE - NE LE CHANGEZ PAS

def render_frame_panel(frame: FrameDecoder, is_best: bool = False):
    border = "green" if is_best else "blue"
    title = f"Frame {frame.frame_id}"

    content = ""
    content += f"Bases lues: {frame.base_count}\n"
    content += f"Codons: {frame.codon_count}\n"
    content += f"Gènes: {len(frame.all_genes)}\n"
    content += f"Score: {frame.get_score()}"

    return Panel(content, title=title, border_style=border)


def render_genome_panel(genome_tape):
    display = " ".join(genome_tape)
    return Panel(display, title="Flux ARN", border_style="cyan")


def render_comparison_table(frames):
    table = Table(title="Comparaison des frames")

    table.add_column("Frame")
    table.add_column("Bases")
    table.add_column("Codons")
    table.add_column("Gènes")
    table.add_column("Score")

    for frame in frames:
        table.add_row(
            str(frame.frame_id),
            str(frame.base_count),
            str(frame.codon_count),
            str(len(frame.all_genes)),
            str(frame.get_score()),
        )

    return table


####################################################
if __name__ == "__main__":
    console = Console()

    genome = (
        "GCUAAGGCCUUGAACCGGAUACCCGUGAAGUUAACCGGGUAACCUAGGCUAAC"
        "GGAUUGCCAAUGGCCUAA"
    )

    decoder = MultiFrameDecoder()
    genome_tape = []

    console.print("\n[MULTI-FRAME GENOME DECODER]\n")

    with Live(console=console, refresh_per_second=6) as live:
        for base in genome:
            genome_tape.append(base)
            decoder.process_base(base)

            best_frame = decoder.get_best_frame()

            genome_panel = render_genome_panel(genome_tape)

            frame_panels = [
                render_frame_panel(frame, is_best=(i == best_frame))
                for i, frame in enumerate(decoder.frames)
            ]

            layout = Layout()
            layout.split_column(
                Layout(genome_panel, size=5),
                Layout(Columns(frame_panels, equal=True), size=10),
            )

            live.update(layout)
            time.sleep(0.25)

    console.print("\nAnalyse terminée\n")
    console.print(render_comparison_table(decoder.frames))

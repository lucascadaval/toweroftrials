# 🏰 Tower of Trials

**Tower of Trials** é um RPG Tático de Turnos "UI-Driven" com uma estética retrô fortemente inspirada nos sistemas clássicos como o Commodore 64 e interfaces minimalistas. 

Em vez de focar em exploração de mapas e andanças intermináveis, *Tower of Trials* corta diretamente para a ação estratégica. Você gerencia seus Pontos de Ação (AP), habilidades e inventário através de uma interface limpa e imersiva para enfrentar hordas de monstros e chefes implacáveis em uma torre de 8 andares.

---

## 📸 Screenshots

<p align="center">
  <img src="assets/docs/images/screenshot_hub_placeholder.png" width="45%" alt="Hub Screen" />
  <img src="assets/docs/images/screenshot_combat_placeholder.png" width="45%" alt="Combat Screen" />
</p>
<p align="center">
  <img src="assets/docs/images/screenshot_inventory_placeholder.png" width="45%" alt="Inventory Screen" />
  <img src="assets/docs/images/screenshot_loot_placeholder.png" width="45%" alt="Loot Popup" />
</p>

---

## ⚔️ Sobre o Jogo

Você foi "invocado" como um Contratante em uma torre que serve como purgatório. Para recuperar sua mortalidade, você deve ascender até o topo.

### Mecânicas Principais:
- **Combate Assimétrico**: O jogador é poderoso e possui até 4 Pontos de Ação (AP) por turno, permitindo combos e múltiplas ações. Os inimigos, por outro lado, são numerosos mas possuem ações limitadas.
- **Progressão Linear**: O jogo se divide em andares (biomas). Cada andar possui Dungeons (para *farm* de níveis e equipamentos) e uma Boss Room. Não é possível retornar a um andar depois de vencer seu chefe!
- **Loots e Atributos**: Colete armas, armaduras, escudos e anéis. Gerencie sua defesa, velocidade (`Speed` define a ordem dos turnos) e saúde.
- **Habilidades Permanentes**: Derrotar os chefes da torre concede Habilidades únicas e feitiços para usar na sua escalada.

---

## 💻 Tecnologias e Arquitetura

Este jogo foi desenvolvido focado em ser leve, rápido e extensível.

- **Linguagem**: Java 17+
- **Game Engine**: [LibGDX](https://libgdx.com/) (Lwjgl3)
- **Interface Gráfica**: [VisUI](https://github.com/kotcrab/vis-ui) (UI customizada temática)
- **Arquitetura de Dados**: [Ashley ECS](https://github.com/libgdx/ashley) (Entity Component System) para o sistema de combate, unificado com um modelo de domínio rígido (`GameEntity`, `Player`, `Enemy`).
- **Build System**: Gradle

---

## 🎮 Onde Roda?

O jogo foi otimizado como uma aplicação Desktop *standalone*.
- **Plataformas**: Windows, macOS, Linux.
- **Controles**: 100% Mouse (Point-and-Click).
- **Resolução Nativa**: 1280x720 (suporta redimensionamento e tela cheia).

---

## 🛠️ Como Rodar / Compilar

Para rodar ou compilar o jogo diretamente do código-fonte, você precisará do **Java 17 (JDK)** instalado na sua máquina.

### Executando o Jogo
Você pode executar o jogo rapidamente através do terminal utilizando o Gradle Wrapper incluso no projeto:

**Windows**:
```bat
gradlew.bat lwjgl3:run
```

**macOS / Linux**:
```bash
./gradlew lwjgl3:run
```

### Compilando um .JAR Executável
Para gerar um arquivo `.jar` que pode ser distribuído e executado em qualquer computador com Java:

**Windows**:
```bat
gradlew.bat lwjgl3:jar
```

**macOS / Linux**:
```bash
./gradlew lwjgl3:jar
```
O executável compilado estará disponível na pasta: `lwjgl3/build/libs/`.

---

## 📜 Licença e Créditos
*(Espaço reservado para licença open-source, agradecimentos e créditos de assets gráficos/áudio utilizados)*

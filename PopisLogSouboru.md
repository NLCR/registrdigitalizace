# Popis logovacího suboru #

## Statistika ##
V úvodu logovacího souboru je vždy statistika obsahující položky:
  * Celkem záznamů
  * Nově založených záznamů
  * Aktualizovaných záznamů
  * Záznamů ve stavu revize
  * Odmítnutých záznamů

```

Celkem záznamů: 3
Nově založených záznamů: 3
Aktualizovaných záznamů: 0
Záznamů ve stavu Revize: 3
Odmítnutých záznamů: 0

```

## Výpis problematických záznamů ##
Následuje výpis problematických záznamů navzájem oddělených horizontální čárou(----------------------------).
  * Na prvním řádku je vždy uvedena výjimka, která u záznamu nastala (Revize, Odmítnuto).
  * Následuje výpis základních popisných polí záznamu.
  * Někdy i seznam záznamů, které jsou vůči záznamu duplicitní.

```

---------------------------------------------------
REVIZE: duplicitní čČNB:
SIGLA:           ABA007
Pole001:         XXX0003x
čČNB:            [cnb999, cnb998, cnb997]
ISBN:            [999XXXa, 111XXXa]
ISSN:            [(Váz.)a, XXX999a, XXX111a, hlavaa, XXX222a]
Čár. kód:        exemplar b Ax
Signatura:       exemplar c Ax
Název:           XXX - Malá preludia aa Název N Název P
Autor:           Pražák, Přemysl
Rok(y) vydání:   1981
Rok periodika:   exemplar y Ax
Část/Díl/Ročník: exemplar v Ax

Seznam záznamů v RD.CZ proti, kterým byla nalezena duplicita:
Pr000063476
Pr000063477
Pr000063478

---------------------------------------------------

```
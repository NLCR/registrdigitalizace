# Návod na použití importního programu #

Importní program běží na serveru, kde čeká na příchozí data, která postupně importuje. Uživatelé proto nespouští na svém počítači žádný program, ale pouze nahrají data na příslušné místo.


## Jak nahrát data ##
Importní program byl nakonfigurován tak, aby si bral testovací data ze stejného FTP serveru, kam jsou nyní nahrávána ostrá data. Pro účely testování byly v kořenovém adresáři FTP zřízeny specifické adresáře, do kterých se data budou nahrávat. Zmíněné adresáře jsou „unitestCSV“ a „unitestMARCXML“. První slouží pro importování CSV souborů (excel) a druhý pro XML soubory obsahující data ve formátu marc-xml (soubory posílané z Aleph).

### Připojení na FTP server: ###
FTP: sluzby.incad.cz <br>
Port: 21 <br>
Login a heslo se liší dle instituce.<br>
<br>
<h3>Syntaxe názvu souboru:</h3>
Nahrané soubory musejí mít správný název, jelikož ten slouží jako nositel některých informací. <br>
<b>Syntaxe:</b> XXXXXX_Y_z.přípona <br>
<ol><li>XXXXXX – Sigla knihovny <br>
</li><li>Y – stav posílaných dat.<br>
<ul><li>Z – záměr <br>
</li><li>P – probíhá <br>
</li><li>H – hotovo <br>
</li></ul></li><li>Z – poznámka k souboru, která není povinná, ale podtržítko za Y povinné je.</li></ol>


<h3>Příklady:</h3>
<pre><code>	ABA001_Z_periodika18stol.csv – správně<br>
	ABA003_P_.xml – správně<br>
	ABA101_K_.xml – špatně (nesprávný stav K)<br>
	ABA204_Hotovo_.csv - špatně (nesprávný stav Hotovo)<br>
	ABA187_H.xml – špatně (chybí podtržítko za stavem)<br>
</code></pre>

<h2>Nové agendy v Relief</h2>
V aplikaci Relief vznikly nové agendy, které si lze přidat na křižovatku klasicky přes ikonku nastavení.<br>
<b>Importy dat</b> – Obsahuje informace o zpracovaných importech.<br>
Nastavení - Registr Digitalizace > Importy dat<br>
<b>Přispěvatel</b> – Obsahuje data o uživatelích, kteří mohou do Registru digitalizace přispívat.<br>
Nastavení - Registr Digitalizace > Přispěvatel<br>
<b>Digitální knihovna</b> – Obsahuje seznam digitálních knihoven jednotlivých přispěvatelů.<br>
Nastavení - Registr Digitalizace > Dynamické listy > Digitální knihovna<br>
<b>Katalog</b> – Obsahuje seznam katalogů jednotlivých přispěvatelů.<br>
Nastavení - Registr Digitalizace > Katalog<br><br>
Do agend Přispěvatel, Digitální knihovna a Katalog bude smět zasahovat jen Administrátor nebo osoba k tomu pověřená. Zůstává tedy agenda Importy dat, kterou budeme využívat pro kontroly importů.<br>
<br>
<h2>Kontrola importů</h2>
Po tom, co jsme si přidali agendy Importy dat na křižovatku, do ní můžeme vstoupit.<br>
<h3>Pole:</h3>
<ul><li>Přispěvatel – Reference do agendy Přispěvatel vzniklá na základě jména souboru<br>
</li><li>Parametry importu – Parametry importu, jako odkud data dorazila atp.<br>
</li><li>Příchozí data – Soubor s příchozími daty (xml, csv…)<br>
</li><li>Upravená data – prozatím nepoužito.<br>
</li><li>Logy – Soubor s informacemi o proběhlém importu ve formátu TXT (prostý text)<br>
</li><li>Poznámka k záznamu – Poznámka pro uživatele, kam si může zapsat cokoli.</li></ul>

Pro kontrolu importu si stáhneme soubor z pole Logy. Soubor otevřeme a přečteme si informace o proběhlém importu. Soubor obsahuje všechny informace, které importní program během procesu zaznamenal. Odmítnuté záznamy, záznamy ve stavu revize atd (viz. <a href='PopisLogSouboru.md'>Popis logovacího souboru</a>). Krom prohlédnutí logu je potřeba podívat se na naimportovaná data v agendě Předloha, jestli jsou záznamy v pořádku.<br>
<br>
<br>
<h2>Další informace</h2>
<b>Délka importu</b> není přesně definována, mimo jiné závisí na objemu importovaných dat. Čím větší objem dat tím delší dobu import dat probíhá. Importní program běží jako vícevláknová aplikace, kdy každé vlákno dělá něco a každé vlákno to dělá v určitých intervalech. Testovací provoz Importního programu bude nastaven na velmi rychlé opakování činností jednotlivých vláken a tak by import jednoho souboru neměl trvat déle než 5 minut.<br>
<br>
<blockquote><hr />
<h2>Související odkazy:</h2></blockquote>

<ul><li><a href='ImportniProgramSchema.md'>Schema rozhodovacího procesu při importu záznamu</a>
</li><li><a href='PopisLogSouboru.md'>Popis logovacího souboru</a>
</li><li><a href='ImpotniProgramParsovaniXML.md'>Parsování XML</a>
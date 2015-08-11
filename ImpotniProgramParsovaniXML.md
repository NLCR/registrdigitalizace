# Impotní program - Parsování XML #


### Druh dokumentu ###
"FMT" - druh dokumentu <br>
<br>
<h3>Pole 001</h3>
"001" - identifikatorZaznamu <br>
<br>
<h3>Sysno</h3>
"SYS" - sysno <br>
<br>
<h3>Název podnázev</h3>
"245a" <br>
"245b" - podnázev <br>
"245n" <br>
"245p" <br>
název = "245a" + "245n" + "245p" - ořezávají se mezery na začátku a na konci + se na konci ořezávají znaky ":/".<br>
<br>
<h3>Variantní názvy</h3>
"246a" - variantní název <br>
<br>
<h3>Autoři</h3>
"100a" - autor <br>
<br>
<h3>Vydavatelské údaje</h3>
"260a" - místo vydání - ořezávají se mezery na začátku a na konci + se na konci ořezávají znaky ":/".<br>
"260b" - vydavatel - ořezávají se mezery na začátku a na konci + se na konci ořezávají znaky ":/".<br>
"260c" - roky vydání <br>
<br>
<h3>Sigla</h3>
"910a" - sigla <br>
<br>
<h3>Počet stran, výška knihy</h3>
"300a" - počet stran <br>
"300c" - výška knihy <br>
<br>
<h3>Číslo ČNB</h3>
"015a" - platné číslo ČNB <br>
"015z" - neplatné číslo ČNB <br>
<br>
<h3>ISBN</h3>
jsou akceptovány pouze hodnoty obsahující pouze znaky 0-9. <br>
"020a" - platné ISBN <br>
"020z" - neplatné ISBN <br>
"020y" - neplatné ISBN <br>
<br>
<h3>ISSN</h3>
"022a" - platné ISSN <br>
"022z" - neplatné ISSN <br>
"022y" - neplatné ISSN <br>
<br>
<h3>URL</h3>
"856u" <br>
V případě seriálu - urlNaTitul <br>
V ostatních případech - urlCastRokRocnik <br>
<br>
<h3>Exemplář</h3>
"ITMb" - čárový kód <br>
"ITMc" - signatura <br>
"ITMd" - poznámka <br>
"ITMv" - svazek/ročník <br>
"ITMi" - číslo <br>
"ITMy" - rok <br>
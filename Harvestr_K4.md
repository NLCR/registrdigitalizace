# Sklízení záznamů z digitálních knihoven #

Registr digitalizace nabízí možnost v pravidelných intervalech sklízet informace o digitalizovaných objektech ze vzdálených knihoven do agendy Digitální objekt.

Současná implementace procesu sklízení využívá protokol [OAI-PMH](http://www.openarchives.org/OAI/openarchivesprotocol.html) a umožňuje snadno připojit knihovny s instalovaným systémem [Kramerius 4](http://code.google.com/p/kramerius/). Jako formát pro popis digitalizovaných objektů je využit standard [MODS](http://www.loc.gov/standards/mods/). Pro přehlednost a atraktivnost výsledků vyhledávání sklizených metadat systém poskytuje možnost automaticky sklidit také obrázkové náhledy.

Z důvodu minimalizace zátěže systémové infrastruktury integrovaných knihoven, je proces nastaven tak, aby sklízel změny metadat digitálních objektů pouze inkrementálně.

Proces sklízení je do budoucna snadno rozšiřitelný o další formáty metadat.
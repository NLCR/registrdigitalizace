# Webová služba Registru digitalizace CZ #

Webová služba poskytuje informace o uložených záznamech Registru digitalizace prostřednictvím protokolu SOAP. Popis jednotlivých záznamů lze získat ve formátech Marc XML, MODS nebo DC. Pomocí služby lze také zjistit stav digitalizace požadovaného záznamu, případně tento stav změnit. Přístup ke službě je omezený. Oprávnění lze získat u správce registru.

Klienti mohou získat přesný popis služby ve formátu [WSDL](http://registrdigitalizace.cz/soapservices/DigitizationRegistryService?wsdl). Pomocí tohoto lze snadno vygenerovat klientskou aplikaci pro automatizovaný přístup ke službě.

## Popis operací ##

V současné době jsou implementovány následující operace.

  * **findRecords** - vrací seznam záznamů v požadovaném formátu. Standardně Marc XML.
    * vstup:
      * **query** - dotaz omezující nalezené záznamy podle následujících kritérií: čísla ČNB, čárkového kódu, ISBN, ISSN, titulu, signatury, roku vydání nebo ročníku periodika. Je nutné zadat alespoň jedno kritérium.
      * **format** - formát popisu záznamu (Marc XML, MODS v3.3, MODS v 3.4, DC RDF). Standardně jsou data uložena ve formátu Marc XML. Parametr je nepovinný.
      * **maxResults** - limit pro počet vrácených záznamů. Parametr je nepovinný.
    * výstup je seznam nalezených záznamů složených z:
      * **recordId** - identifikátor záznamu
      * **state** - stav digitalizace záznamu
      * **descriptor** - popis záznamu v požadovaném formátu

  * **getRecordState** - vrací aktuální stav digitalizace záznamu
    * vstup:
      * **recordId** - identifikátor záznamu
    * výstup:
      * **state** - stav digitalizace

  * **setRecordState** - zapíše nový stav digitalizace záznamu
    * vstup:
      * **recordId** - identifikátor záznamu
      * **newState** - nový stav digitalizace záznamu
      * **oldState** - původní stav digitalizace záznamu. Viz. operace getRecords a getRecordState
      * **user** - operátor, který digitalizoval záznam
      * **date** - datum digitalizace
    * výstup:
      * **result** - potvrzení úspěšného zápisu. Zápis může selhat z důvodu souběžného zápisu jiným klientem služby.


  * **setRecordUrnNbn** - nastaví pro daný záznam v registru digitalizace přidělená URN:NBN. Dříve přidělená URN:NBN se přepíší předanými hodnotami.
    * vstup:
      * **recordId** - identifikátor záznamu. Viz. oprace findRecords
      * **date** - datum přidělení URN:NBN. Když chybí použije se aktuální datum.
      * **urnNbnList** - seznam přidělených URN:NBN pro daný záznam. Počet položek může být v rozsahu 0-1000
    * výstup:
      * **return** - potvrzení úspěšného zápisu. Zápis může selhat z důvodu nenalezeného záznamu

  * **addRecordUrnNbn** - přidá k danému záznamu v registru digitalizace přidělená URN:NBN. Dříve přidělená URN:NBN se ponechají.
    * vstup:
      * **recordId** - identifikátor záznamu. Viz. oprace findRecords
      * **date** - datum přidělení URN:NBN. Když chybí použije se aktuální datum.
      * **urnNbnList** - seznam přidělených URN:NBN pro daný záznam. Počet položek může být v rozsahu 1-1000
    * výstup:
      * **return** - potvrzení úspěšného zápisu. Zápis může selhat z důvodu nenalezeného záznamu
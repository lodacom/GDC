GDC Project
=====================================
Notre projet profite pleinement des toutes les dernières technologies du marché. Tout d'abord notre
application s'appuie sur le [framework Play v2.2.1](http://www.playframework.com). Il met en 
place plusieurs types de persistences:

1. Nous utilisons [Oracle 12c Release 1 Entreprise Edition](http://www.oracle.com/technetwork/database/enterprise-edition/downloads/index.html)
qui nous permet de mettre en place une <b>base de données de type relationnelle</b>. Cette dernière
contient les données de [l'INSEE](http://www.insee.fr/fr/bases-de-donnees/) 
et de l'ISF 2002 et 2008.

2. Un <b>triple store de type TDB</b> est géré de façon interne à l'application grâce à [l'API Jena](http://jena.apache.org/).
Ce dernier contient les coordonnées GPS récupérées de [Geonames](http://www.geonames.org/)
de toutes les régions, de tous les départments et de certaines villes (les plus importantes).

3. Une <b>base de données de type graphe</b> s'imbrique à cet eco-système. Nous avons choisi
d'utiliser [Neo4J](http://www.neo4j.org/). Celle-ci contient pour chaque région, département et ville
l'abstract donné par [DBPedia](http://dbpedia.org/About).

4. Une <b>base de données de type colonne</b> vient s'ajouter pour compléter notre collection.
Nous avons choisi d'utiliser [HBase](http://hbase.apache.org/).

Objectif du projet
------------------
Fusionner toutes ces données hétéroclites pour délivrer un service à l'utilisateur. A travers
ce projet nous voulons montrer l'évolution des flux des riches et la comparer l'évolution 
des personnes qui persoivent le RSA donc des personnes dans la précarités. Toutes ces 
données évidemment en fonction des régions, départements et villes.

Détails techniques pour les développeurs
----------------------------------------
<p>
Si vous voulez tester notre projet, il faut évidemment installer tous les logiciels cités plus haut. De plus, et 
en particulier pour Neo4J, un jeu de données a été prévu pour pouvoir effectuer ces tests. Pour se faire 
vous devez construir un nouveau projet. Puis copier les trois fichiers présents (NeoManager, OracleRequest, 
Test) dans /public/util dans le projet que vous venez de créer, plus le fichier de mapping dans 
/public/ressources et NeoOntology présent dans le package models. 
</p>
<p>
Si vous avez déjà des données dans Neo4J ne décommenter pas la ligne n°17 dans 
la classe NeoManager, par contre si vous ne tenez pas à vos données présentes décommenter cette ligne et 
changer le path à la ligne n°14. Ensuite ceci fait, exécuter la classe Test qui contient un main, ce dernier va insérer les données
des régions (si vous voulez insérer plus de données il vous suffit de décommenter les lignes appropriées à partir
du n°69 dans la classe Test) et générer un fichier neo.properties. Vous devez <b>absolument</b> copier ce fichier dans 
/public/ressources (il permettra par la suite de retrouver la racine de l'arborescence).
Pourquoi vous ne pouvez pas exécuter Test depuis notre projet? En effet la question se pose. Il se trouve que les jars
présents dans les imports rentrent en conflits en ce qui concerne l'insertion, c'est pourquoi vous devez créer un 
projet à part.
</p>
<p>
Pour intégrer les données de HBase, vous devez également créer un projet à part (pour les mêmes raisons).
Copier les deux fichiers dans /public/util (HBaseInit et HBaseManager) et les deux jeux de données
présents dans /public/ressources (Donnees_Departement_RSA.csv et Donnees_Region_RSA.csv).
Puis exécuter la classe pricipale qui est HBaseInit (ne pas oublier de lancer le serveur HBase avant).
</p>

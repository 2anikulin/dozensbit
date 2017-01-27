# About DozensBit
## What is it?
This is Java implementation of in-process search-engine. It designed to perform queries on big set of objects with various attributes. It supports complex queries and may combine and intersect sets of objects very fast.
## The Problem definition

Imagine - you have set of structures with different number of attributes:
```
User0 : {
  Name : John,
  Age : 32,
  City : [Berlin, Dresden],
}

User1 : {
  Name : Ann,
  Gender : female,
  City : NY,
  HasChildren: true
}
```
And you need run this query:
```
SEARCH User WHERE (City = NY OR City = Berlin) AND Gender = female
```
DozensBit cache able to handle it! It fast, small and simple solution.

## Short example
```
Cache<String> cache = new IndexedCache<String>();

MultiValueMap attributes1 = new MultiValueMap();
attributes1.put("name", "john");
attributes1.put("age", "32");
attributes1.put("city", "berlin");
attributes1.put("city", "dresden");

cache.put("This is user #1", attributes1);

//Or you can use Utils and JSON
cache.put("This is user #2", Utils.toMap("{name:ann,gender:female,city:ny}"));

cache.commit();

QueryBuilder builder = cache.createQuery();

QueryBuilder.Query query = builder
                .start("city", "ny")
                .or("city", "berlin")
                .and("gender", "female")
                .get();

List<String> result = cache.find(query);
```

## Where i can use it?
In application where you need to analyze millions of objects with different structure. And low latency is very important.
## Why it fast?
It uses a binary indexes for search. And it doesn't have out-process object serialization / deserialization.
## What about schema?
Schema-less. You can use various set of attributes for each object
## Is it thread-safe?
Yes. It designed for using in concurrent environment.
## What about benchmark?
Usually benchmarks are very subjective. There are a lot of factors may influence on results like a number of cached objects, number of objects in result output, complexity of query, multi-threading, hardware, etc. So i tried to chose trade off for all players. 

We will compare in single-thread mode: 
* DozensBit, 
* H2 in-memory database, 
* PostgresSQL (on localhost)

Data:
* 1 000 000 Objects with 10 fields
* Number of objects in output result: 100 000  (1/10), 1000 000 (1/1)
* Query: 
```
  SELECT * FROM DB WHERE ATTR_1='omsk' AND 
                         ATTR_2='tomsk' AND 
                         ATTR_3='novosibirsk' AND 
                         ATTR_4='moscow' AND 
                         ATTR_5='male' AND 
                         ATTR_6='ru' AND 
                         ATTR_7='de' AND 
                         ATTR_8='en' AND 
                         ATTR_9='au' AND 
                         ATTR_10='it' 

```
* Hardware: Laptop Intel Core i7-4700HQ CPU 2.40GHz × 4, RAM 8GB

Results:

Objects in output result: 100 000  (1/10)
```
-----------------------------
| Storage  | Query time (ms) |
-----------------------------
| DozensBit|    2            |
-----------------------------
| H2       |    61           |
-----------------------------
| Posgres  |    720          |
-----------------------------
```
Objects in output result: 1 000 000  (1/1)
```
-----------------------------
| Storage  | Query time (ms) |
-----------------------------
| DozensBit|    9            |
-----------------------------
| H2       |    210          |
-----------------------------
| Posgres  |    2954         |
-----------------------------
```
# Getting started
## Setting Up Maven
There are two ways to include Dozensbit in your project.<br>
You can clone project from GitHub. Build and install to local maven repository
```
git clone git@github.com:2anikulin/dozensbit.git
mvn clean install
```
Or you can download jar file and install to local maven repository
```
wget https://dl.dropboxusercontent.com/u/40772133/dozensbit-1.0/dozensbit-cache.1.0.jar
mvn install:install-file -Dfile=dozensbit-cache.1.0.jar -DgroupId=net.dozensbit.cache -DartifactId=cache -Dversion=1.0 -Dpackaging=jar -DgeneratePom=true
```

After it, add dependency to your pom.xml

```
<dependency>
    <groupId>net.dozensbit.cache</groupId>
    <artifactId>cache</artifactId>
    <version>1.0</version>
</dependency>
<dependency>
     <groupId>commons-collections</groupId>
     <artifactId>commons-collections</artifactId>
     <version>3.2.1</version>
</dependency>
<dependency>
      <groupId>org.json</groupId>
      <artifactId>json</artifactId>
      <version>20141113</version>
</dependency>
```
That's it!

## Cache usage example
### Fill data
```
Cache<String> cache = new IndexedCache<String>();

MultiValueMap attributes1 = new MultiValueMap();
attributes1.put("name", "john");
attributes1.put("age", "32");
attributes1.put("city", "berlin");
attributes1.put("city", "dresden");

cache.put("This is user #1", attributes1);

//Or you can use Utils and JSON
cache.put("This is user #2", Utils.toMap("{name:ann,gender:female,city:ny}"));

//Only after commit this new/modified data will be available
cache.commit(); 
```
### Modify data
```
//put always set new data
cache.put("This is user #3", Utils.toMap("{name:ann,gender:female,city:ny}"));

//remove object with attributes
cache.remove("This is user #3");

//remove all objects with their attributes
cache.deleteAll();

//Don't forget commit changes. 
//Only after commit it will be available in search cache 
cache.commit();
```
### Query data
**Simple query**<br> 
SELECT * WHERE city = "berlin" OR city = "amsterdam"
```
QueryBuilder.Query query = cache.createQuery()
                .start("city", "berlin")
                .or("city", "amsterdam")
                .get();

List<String> result = cache.find(query);
```
**Complex query**<br> 
SELECT * WHERE (city = "berlin" OR city = "amsterdam") AND (lang = "ru" OR lang = "en")
```
QueryBuilder.Query query = cache.createQuery()
               .start(
                        cache.createQuery()
                                .start("city", "berlin")
                                .or("city", "moscow")
                                .get()
                ).and(
                        cache.createQuery()
                                .start("lang", "ru")
                                .or("lang", "en")
                                .get()
                )
                .get();

List<String> result = cache.find(query);
```
**Soft equal**<br>
The operands "AND", "OR" applied to objects which have searched attribute. If object hasn't searched attribute this operands don't applied and object won't be in result output. There is use-case when you need find all objects "with" and "without" searched attribute, however if attribute exist it must be equal to specified value.

Rules:
If attribute exists and equal to given value **softEqual** return **true**<br> 
If attribute DOES'N exists **softEqual** also return **true**<br> 
And only If attribute exists and NOT equal to given value **softEqual** return **false** 

Example:<br>
1) { name : Elen, city : NY, gender : female}<br>
2) { name : Ann, city : NY}<br>
3) { name : Vlad, city : NY, gender : male}<br>

The result of softEqual("gender", "female") return 1,2

```
SELECT * WHERE city="omsk" AND (
                 IF (gender EXISTS) AND (gender = "female") -> return true
                 ELSE IF (gender EXISTS) AND (gender != "female") -> return false
                 ELSE return true)

QueryBuilder.Query query = cache.createQuery()
                .start("city", "omsk")
                .softEqual("gender", "female")
                .get();

List<String> result = cache.find(query);
```

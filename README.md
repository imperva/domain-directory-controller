# DDC - Domain Directory Controller
DDC is an Active Directory Java SDK designed to simplify AD interaction for small, medium and large projects. 
DDC is a portable Java library provided with a friendly API, allowing you to quickly compose simple or complicated 
queries against AD's endpoints without any previous LDAP knowledge.

# Benefits
The goal of this library is to enable an easy interaction with any LDAP Directory Server 
by hiding all the related communication's issues and internal bits-and-bytes.

Internally DDC makes use of apache directory ldap API and adds the following enhancements:

- Assemble ldap queries easy via ab Object Oriented syntax instead of concatenating and parsing strings
- Although DDC currently supports Microsoft Active Directory only, it was designed to be easily extended to fit any other Active Directory implementation 
- Easy Paging API 
- Querying multiple endpoints with a single query
- Change Requests: Add, Remove, Replace AD's objects
- Secured connection
- Automatically retries in case of failure
- Automatically resolve host to IP
- Support search in Multiple AD servers
- Support Primary & Secondary AD servers
- Easy API to retrieve data for each endpoint in query
- Helpful builtin functionalities: 
    - Test Connection
    - Get users' groups recursively
    - Authentication 
    - Auto retry on Connection Failure
    - etc.
 

# Dependencies
Internally DDC makes use of a few libraries:
- junit 4.12
- mockito 1.9.5
- log4j 1.7.6
- apache directory ldap API 1.0.0-RC2

# Project Structure
In previous DDC versions DDC was composed of four different modules: ddc-core, ddc-api, ddc-service and ddc-common.
From now on DDC is composed of only two modules: ddc-core & ddc-service.

**ddc-core - (required)**
This module encapsulates the core communication logic: 
- Managing Connection life-cycle
- Handles paging sessions
- Exception handling
- Securing connection
- Executing the queries

**ddc-service - (optional)**
This module is an optional layer designed to ease your first steps with DDC even more.
This module contains several useful methods implemented by using *ddc-service*. 
These methods cover some common main needs for your convenience:   
- isMemberOf - Finds recursively the groups of the given user based on a pre-defined group list to be checked
- getUsersInGroup - Finds recursively all groups' members of the given group list
- testConnection - Tests endpoint's connection
- authenticate(.., boolean allowEnabledOnly,...,) - Authenticate endpoint based on given credentials taking/not-taking in consideration disabled users 
- etc.

All the methods implemented in this layer can be implemented directly by using ddc-service, ddc-service is just a collection of useful shortcuts.

For your convenience, the project's binaries can be found in the 'bin' directory.

# How To Use It - First Steps
In order to start playing with DDC you just need to import all DDC's projects and dependencies (see Dependencies section).
DDC is a Maven project (binaries are deployed in Maven Central) so you can import the projects manually or via Maven by adding the following Dependencies to your project's POM file:

~~~

       <!--To work directly with ddc API add the following dependency-->
        <dependency>
            <artifactId>ddc-core</artifactId>
            <groupId>com.imperva.opensource.ddc</groupId>
            <version><VERSION></version>
        </dependency>
       <!--To work with ddc service wrapper add the following dependency-->
        <dependency>
            <artifactId>ddc-service</artifactId>
            <groupId>com.imperva.opensource.ddc</groupId>
            <version><VERSION></version>
        </dependency>
    
~~~

# Samples

#### Starting Kit
All the code snippets below are available as part of DDC's 'StartingKit' project. The project can be found in the 'startingkit' directory & downloaded for your convenience. Enjoy!

#### Use Case 1 - Authentication

```Java
Endpoint endpoint = new Endpoint();
endpoint.setSecuredConnection(false);
endpoint.setSecuredConnectionSecondary(false);
endpoint.setPort(389);
endpoint.setSecondaryPort(389);
endpoint.setHost("10.100.10.11");
endpoint.setSecondaryHost("10.100.10.100");
endpoint.setPassword("somepass");
endpoint.setUserAccountName("domain\\user"); //* You can use the user's Distinguished Name as well

ConnectionResponse connectionResponse = DirectoryConnectorService.authenticate(endpoint);
boolean succeeded = !connectionResponse.isError();

...
```

#### Use Case 2 - Query all users' phone number and city of users that their first name is 'Gabriel'

```java
...

//* Create a new Endpoint (see Use Case 1)

QueryRequest queryRequest = new QueryRequest();
queryRequest.setDirectoryType(DirectoryType.MS_ACTIVE_DIRECTORY);
queryRequest.setEndpoints(new ArrayList<Endpoint>(){{add(endpoint);}});
queryRequest.setSizeLimit(1000);
queryRequest.setTimeLimit(1000);

queryRequest.setObjectType(ObjectType.USER);
//* Shortcut. Internally will add the relevant LDAP script to filter out any non human Entry (printers, machines etc.)

queryRequest.addRequestedField(FieldType.EMAIL);
queryRequest.addRequestedField(FieldType.CITY);

QueryAssembler queryAssembler;
queryAssembler = new QueryAssembler();
Sentence firstNameSentence = queryAssembler.addPhrase(FieldType.FIRST_NAME, PhraseOperator.EQUAL, "Gabriel").closeSentence();

queryRequest.addSearchSentence(firstNameSentence);

QueryResponse queryResponse;
try(Connector connector = new Connector(queryRequest)) {
    queryResponse = connector.execute();
}

List fields = queryResponse.getAll().stream().map(res -> res.getValue()).collect(Collectors.toList());
System.out.println("Use Case 2 - Query all users' phone number and city of users that their first name is 'Gabriel': " + fields.size());

...
```

#### Use Case 3 -  QueryResponse structure and data manipulation
QueryResponse contains all the data (and exceptions on failure cases) fetched during the query execution.


QueryResponse's data is structured as following:

**PartitionResponse**

<p>
A list of PartitionResponse, each PartitionResponse encapsulates the data of a particular endpoint. 
In other words, if you execute a query against two endpoints you will then have two PartitionResponse objects in the list. To get the PartitionResponse list you can use the get() method: i.e. queryResponse.get();
</p>

**EntityResponse**

<p>
Inside each PartitionResponse you will find (among other properties) a list of EntityResponse, Each EntityResponse contains a single return value.
For example, say you execute a query to retrieve all users' phone number that their first name is 'Gabriel', then for each user (Gabriel Battistuta, Gabriel Beyo etc.) an EntityResponse instance
is added to the list. To get the a unified list of all EntityResponses of all PartitionResponse object you can use the getAll() method: i.e. queryResponse.getAll();
</p>

**Fields**

<p>
The actual values are stored inside each EntityResponse instance, inside a List of Field. The list of Fields contains the various attributes (mail, phone number, city, department etc.) requested in the query per each fetched member.
</p>

```java
...

QueryResponse queryResponse;
try(Connector connector = new Connector(queryRequest)) {
    queryResponse = connector.execute();
}

System.out.println("Use Case 3 - QueryResponse structure and data manipulation:");
for (EntityResponse entityResponse : queryResponse.getAll()) {
    for (Field field : entityResponse.getValue()) {
        System.out.println("Val: " + field.getValue());
        }
}

...
```

#### Use Case 4 - Paging
DDC expose a friendly paging API, useful in cases you have a large amount of data returned from your query and you want to handle the results chunk by chunk.
you can set the sizes of the chunk by using this setter setPageChunkSize(x).

*NOTE - Maximum result set of AD servers is usually 1000 entries, so if you suspect your query will result in a larger amount of data you will find paging very useful*

```java

//* Create a new Endpoint (see Use Case 1)

QueryRequest queryRequest = new QueryRequest();
queryRequest.setDirectoryType(DirectoryType.MS_ACTIVE_DIRECTORY);
queryRequest.setEndpoints(new ArrayList<Endpoint>(){{add(endpoint);}});
queryRequest.setSizeLimit(1000);
queryRequest.setTimeLimit(1000);

queryRequest.setPageChunkSize(100);

queryRequest.addSearchSentence(new QueryAssembler().addPhrase(FieldType.FIRST_NAME, PhraseOperator.EQUAL,"Gabriel").closeSentence());

QueryResponse result = new QueryResponse();
try(Connector connector = new Connector(queryRequest)) {
    Cursor cursor = connector.getCursor();
    while (cursor.hasNext()) {
        result.addPartitionResponse(cursor.next().get());
    }
}

...
```


#### Use Case 5 - Are you ready?
This example demonstrates how to assemble complicated queries by using dcc's syntax.
Say you want to fetch the Email, City, Phone Number and the Distinguished Name of users which their name results to be "Gabriel" AND that are part of the IT department OR users that their country results to be "Italy".


```java

//* Create a new Endpoint (see Use Case 1)

QueryRequest queryRequest = new QueryRequest();
queryRequest.setDirectoryType(DirectoryType.MS_ACTIVE_DIRECTORY);
queryRequest.setEndpoints(new ArrayList<Endpoint>(){{add(endpoint);}});
queryRequest.setSizeLimit(1000);
queryRequest.setTimeLimit(1000);
queryRequest.setPageChunkSize(100);

queryRequest.addRequestedField(FieldType.EMAIL);
queryRequest.addRequestedField(FieldType.CITY);
queryRequest.addRequestedField(FieldType.PHONE_NUMBER);
queryRequest.addRequestedField(FieldType.DISTINGUISHED_NAME);

QueryAssembler queryAssembler = new QueryAssembler();

//* Create first sentence: users which their name results to be "Gabriel" AND that are part of the IT department 
Sentence nameAndDepSentence = queryAssembler
	.addPhrase(FieldType.FIRST_NAME, PhraseOperator.EQUAL,"Gabriel")
	.addPhrase(FieldType.DEPARTMENT, PhraseOperator.EQUAL,"IT")
	.closeSentence(SentenceOperator.AND);

//* Create the second sentence: users that their country results to be "Italy"
Sentence countrySentence = queryAssembler
	.addPhrase(FieldType.COUNTRY, PhraseOperator.EQUAL,"Italy")
	.closeSentence();

//* Glue the sentences with the OR operator	
Sentence finalSentence = queryAssembler
	.addSentence(nameAndDepSentence)
	.addSentence(countrySentence)
	.closeSentence(SentenceOperator.OR);

queryRequest.addSearchSentence(finalSentence);

QueryResponse result = new QueryResponse();
try(Connector connector = new Connector(queryRequest)) {
    Cursor cursor = connector.getCursor();
    while (cursor.hasNext()) {
        result.addPartitionResponse(cursor.next().get());
    }
}

...
```

#### Use Case 6 - Dynamic Queries
Till now examples were focused on static queries, meaning that the size and type of the queries' part were fixed. 
The following example demonstrates how to assemble **dynamic queries** by using dcc's syntax. Let's complicate a bit *Use Case 5*:
Say you want to use the same previous query but now instead of a single static user name ("Gabriel") you need to parse a dynamic list of users given at runtime.

```java

//* Create a new Endpoint (see Use Case 1)

QueryRequest queryRequest = new QueryRequest();
queryRequest.setDirectoryType(DirectoryType.MS_ACTIVE_DIRECTORY);
queryRequest.setEndpoints(new ArrayList<Endpoint>(){{add(endpoint);}});
queryRequest.setSizeLimit(1000);
queryRequest.setTimeLimit(1000);
queryRequest.setPageChunkSize(100);

queryRequest.addRequestedField(FieldType.EMAIL);
queryRequest.addRequestedField(FieldType.CITY);
queryRequest.addRequestedField(FieldType.PHONE_NUMBER);
queryRequest.addRequestedField(FieldType.DISTINGUISHED_NAME);

QueryAssembler queryAssembler = new QueryAssembler();

//* Create the dynamic sentence: users which their name results to be values of the list 
for(String user : usersList){
	queryAssembler.addPhrase(FieldType.FIRST_NAME, PhraseOperator.EQUAL,user);
}

Sentence usersSentence = queryAssembler.closeSentence(SentenceOperator.OR);
Sentence depSentence = queryAssembler.addPhrase(FieldType.DEPARTMENT, PhraseOperator.EQUAL,"IT").closeSentence();
Sentence nameAndDepSentence  = queryAssembler.addSentence(usersSentence).addSentence(depSentence).closeSentence(SentenceOperator.AND);

//* Create the second sentence: users that their country results to be "Italy"
Sentence countrySentence = queryAssembler
	.addPhrase(FieldType.COUNTRY, PhraseOperator.EQUAL,"Italy")
	.closeSentence();

//* Glue the sentences with the OR operator	
Sentence finalSentence = queryAssembler
	.addSentence(nameAndDepSentence)
	.addSentence(countrySentence)
	.closeSentence(SentenceOperator.OR);

queryRequest.addSearchSentence(finalSentence);

QueryResponse result = new QueryResponse();
try(Connector connector = new Connector(queryRequest)) {
    Cursor cursor = connector.getCursor();
    while (cursor.hasNext()) {
        result.addPartitionResponse(cursor.next().get());
    }
}

...
```
Now imagine performing this change by manually parsing pieces of LDAP's strings:

*(|(&(|(givenName=Gabriel)(givenName=Noam)(givenName=Shahar)(givenName=Mor)(givenName=Assaf)(givenName=Viatly)(givenName=Dror)(givenName=Rina)(givenName=Simcha))(department=IT))(co=Italy))*


#### Use Case 7 - Error Handling

The QueryResponse object is useful also for checking the query's status. In case the query fails for any reason you can retrieve the actual exceptions using the endpoint's IP as key.

```java
...

//* Create a new Endpoint (see Use Case 1)

QueryRequest queryRequest = new QueryRequest();
queryRequest.setDirectoryType(DirectoryType.MS_ACTIVE_DIRECTORY);
queryRequest.setEndpoints(new ArrayList<Endpoint>(){{add(endpoint);}});
queryRequest.setSizeLimit(1000);
queryRequest.setTimeLimit(1000);

queryRequest.addRequestedField(FieldType.EMAIL);
queryRequest.addRequestedField(FieldType.CITY);

QueryAssembler queryAssembler = new QueryAssembler();
Sentence firstNameSentence = queryAssembler.addPhrase(FieldType.FIRST_NAME, PhraseOperator.EQUAL,"Gabriel").closeSentence();

queryRequest.addSearchSentence(firstNameSentence);

//* Exceptions are stored in a key-value structure where the key is the enpoint's IP address
 QueryResponse queryResponse = null;
        try(Connector connector = new Connector(queryRequest)) {
            queryResponse = connector.execute();
        }

for (PartitionResponse partitionResponse : queryResponse.get()) {
    Status status = partitionResponse.getStatus().get("10.100.10.100");
    System.out.println("Exception: " + status.getError());
}

...
```

#### Use Case 8 - Change Requests: Add, Remove, Replace AD's objects 

In order to change AD's object's fields a ChangeRequest object is needed.
Using the ChangeRequest object you can specify the field and values you want to add, remove or replace.

```java
...

//* Create a new Endpoint (see Use Case 1)

ChangeRequest changeRequest = new ChangeRequest("<The Distinguished Name of the AD object to change>");
changeRequest.add(FieldType.CITY, "<value>");//* Add new field with value
changeRequest.remove(FieldType.EMAIL);//* Remove field
changeRequest.replace(FieldType.COUNTRY, "<value>");//* Replace field's value

changeRequest.setEndpoint(endpoint);

try (Connector connector = new Connector(changeRequest)) {
    connector.executeChangeRequest();
}

...
```


# Configuration
DDC contains a single configuration file at this relative location location:
ddc-dal-impl\src\main\resources\ddc-core-properties\communication.properties

In the following sections, we will cover the supported configuration options.

##### Secured Connection
DDC enables querying Directory Servers in secured mode by just setting the setSecuredConnection(true) and setSecuredConnectionSecondary(true) endpoint's properties to true.
By default, DDC ignores all standard certificates validations, in order to change this behaviour set the following flag: ignore.ssl.cert.chain.exception=true to **false**.

##### Authentication Mode
DDC enables setting a default authentication mode. The default mode is set to GSSAPI, in order to change this behaviour set the following flag: authentication.mechanism=GSSAPI to any of the following supported modes:
- CRAM-MD5
- DIGEST-MD5
- GSSAPI
- PLAIN
- NTLM
- GSS-SPNEGO

##### Connection Timeout
DDC enables setting a default timeout to DDC's queries. The default timeout value is set to 5000 seconds, in order to change this behaviour set the following flag connection.timeout=5000 to a different timeout value.


# Getting Help
If you have questions about the library, please be sure to check out the API documentation. 
If you still have questions, reach out me via mail gabi.beyo@imperva.com.

##### Reporting Bugs
Please open a Git Issue and include as much information as possible. If possible, provide sample code that illustrates the problem you're seeing. 
If you're seeing a bug only on a specific repository, please provide a link to it if possible.

Please do not open a Git Issue for help, leave it only for bug reports.

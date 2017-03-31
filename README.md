# csvToDmlGenerator

db 데이터를 덤프받은 csv 파일을 insert 구문의 dml로 생성하는 배치 프로그램

## 개발 동기
- db 데이터를 csv 파일로 덤프 내렸다가 올리려고 하는데 컬럼에 json 형식의 값들이 있어 csv 로 풀어지지 않는 경우가 있어서 개발해보게 됨...

## 구현 기술
- [Maven](https://maven.apache.org) ver. 3 이상
- [Spring Boot(ver. 1.5.2), batch](https://spring.io/guides/gs/batch-processing/)
	
	```
	<dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-batch</artifactId>
    </dependency>
	```
	
- [apache commons](https://commons.apache.org)
	- [collection4](https://commons.apache.org/proper/commons-collections/) ver 4.1
	- [lang3](https://commons.apache.org/lang/) ver 3.5
- [google guava](https://github.com/google/guava) ver 21.0

## 사용방법
- main class : `CsvToDmlGenerateApplication`
	- job test class : `CsvToDmlGenerateApplicationTest`
- csv 파일의 위치, 구분자, 테이블명 등의 정보는 `FileInfoContainer` 인터페이스의 구현체에 선언하도록 되어있다.
- 이를 변경하려면 `FileInfoContainer` 인터페이스의 구현체 클래스를 작성하여`CsvToDmlGenerateJobConfiguration.fileInfoContainer()` 메소드에 해당 클래스의 인스턴스를 리턴하도록 한다.
	- 예제로 `DefaultFileInfoContainer` 를 넣어놓았다.
		- tableName : sample
		- delimeter : ,
		- csvFilePath : 프로젝트 루트경로/sample_data/
- **반드시 csv 파일의 첫번째 라인은 컬럼명이어야한다.(insert 쿼리 문법 위해)**
	- `INSERT INTO(COLUMN_NAME1, COLUMN_NAME2, ...) VALUES(VAL1, VAL2, ...);`
- dml 파일은 지정한 `csvFilePath` 내에 `테이블명.dml` 의 형태로 생성된다.

## 패키지 구조
- `io.github.gogleowner.configuration` : job configuration 에 관한 것
	- 배치 작업 관련 빈들이 선언되어있다.
	- `chunk` 단위는 임의로 100개로 지정하였다. (`CsvToDmlGenerateJobConfiguration.dmlQueryGenerateStep()` 메소드 참고)
- `io.github.gogleowner.item` : reader, processor, writer
	- reader : csv 파일을 읽어서 `Map<String, String>`의 형태로 return 한다.
		- 파일을 읽는 역할을 하기에 `FlatFileItemReader`를 상속받아 구현했다.
		- `Map`의 key는 컬럼명, value는 컬럼의 값이다.
	- processor : insert dml 구문을 생성한다.
		- insert dml 구문 생성시 `StrSubstitutor`을 사용했다.
	- writer : 파일에 write 한다.
		- 파일에 쓰는 역할을 하기에 `FlatFileItemWriter`를 상속받아 구현했다.
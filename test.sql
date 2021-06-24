// read commited
1: create table student (id int, age int, primary key(id));
1: begin transaction;
1: insert into student(id,age) values(1,1);
2: begin transaction;
2: select id,age from student;
1: commit;

// read uncommitted
1: create table student (id int, age int, primary key(id));
1: begin transaction;
1: insert into student(id,age) values(1,1);
2: begin transaction;
2: select id,age from student;
1: commit;
2: commit;

// serializable
1: create table student (id int, age int, primary key(id));
2: begin transaction;
2: select id,age from student;
1: begin transaction;
1: insert into student(id,age) values(1,1);
2: select id,age from student;
1: commit;
2: commit;


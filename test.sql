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

// rollback
1: begin transaction;
1: insert into student(id,age) values(2,1);
1: rollback;
1: select id,age from student;

// rollback savepoint
1: begin transaction;
1: insert into student(id,age) values(3,1);
1: savepoint hello;
1: insert into student(id,age) values(4,1);
1: rollback to savepoint hello;
1: commit;
1: select id,age from student;

// restore
1: begin transaction;
1: create table student (id int, age int, primary key(id));
1: insert into student(id,age) values(1,1);
1: commit;
1: begin transaction;
1: insert into student(id,age) values(2,1);
1: kill;
1: select id from student;

// checkpoint
1: begin transaction;
1: create table student (id int, age int, primary key(id));
1: insert into student(id,age) values(3,1);
1: commit;
1: checkpoint;
1: begin transaction;
1: insert into student(id,age) values(4,1);
1: kill;
1: select id from student;




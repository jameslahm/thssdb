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
1: checkpoint;
1: begin transaction;
1: insert into student(id,age) values(4,1);
1: kill;
1: select id from student;


create table hello (id int not null, age long, charge float, salary double , name string(256),primary key(id))
create table hi (id int not null, age long,name string(256),primary key(id))
create table yello (identifier int not null, name string(256),primary key(identifier))
insert into hello (id, age, charge, salary, name) values(1,1,1.000000,1.000000,'alice')
insert into hello (age, charge, salary, name) values(1,1.000000,1.000000,'bob')
insert into hello (id, age, charge, salary, name) values(2,1,1.000000,1.000000,'bob')
insert into hi (id, age, name) values(1,2,'bob')
insert into hi (id, age, name) values(2,3,'alice')
insert into hi (id, age, name) values(3,4,'cindy')
insert into yello (identifier, name) values(1,'alice'),(2,'flank') 
update hello set id = 3 where id = 1
update hello set id = 1 where id = 3 and name = 'alice'
update hello set age = 2 where name = 'alice' or name = 'bob'
select id from hello
select age from hello
select hello.id, hello.age, hello.name, hi.id from hello join hi on hello.name = hi.name
select hello.id, hello.age, hello.name, hi.id from hello, hi where hello.name = hi.name
select hello.id, hello.age, hello.name, hi.id from hello left outer join hi on hello.name = hi.name
select hello.id, hello.age, hello.name, hi.id from hello right outer join hi on hello.name = hi.name
select hello.id, hello.age, hello.name, hi.id from hello full outer join hi on hello.name = hi.name
select hello.id, hello.age, hello.name, yello.identifier from hello natural join yello
select hello.id from hello,hi,yello where hello.name = hi.name
delete from hello where id = 2
select id from hello
alter table hello add yes int
select * from hello
alter table hello drop column yes
select * from hello
create database course
use course
drop database course
use test
use course
create table student (id int, age int, primary key(id))
begin transaction
insert into student(id,age) values(2,1);
rollback
select id,age from student
begin transaction
insert into student(id,age) values(2,1);
savepoint hello
insert into student(id,age) values(4,1)
rollback to savepoint hello
commit
select id,age from student
begin transaction
insert into student(id,age) values(1,1)
begin transaction
select id,age from student;
commit
begin transaction
insert into student(id,age) values(3,1)
begin transaction
select id,age from student;
commit
begin transaction
select id,age from student;
begin transaction
insert into student(id,age) values(4,1);
select id,age from student;
commit
commit
begin transaction
create table student (id int, age int, primary key(id))
insert into student(id,age) values(5,1)
commit
begin transaction
insert into student(id,age) values(6,1)
select id from student
begin transaction
create table student (id int, age int, primary key(id))
insert into student(id,age) values(5,1)
checkpoint
begin transaction
insert into student(id,age) values(6,1)
select id from student





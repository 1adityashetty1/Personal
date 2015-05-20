-- CS 61A Fall 2014
-- Name:
-- Login:

create table parents as
  select "abraham" as parent, "barack" as child union
  select "abraham"          , "clinton"         union
  select "delano"           , "herbert"         union
  select "fillmore"         , "abraham"         union
  select "fillmore"         , "delano"          union
  select "fillmore"         , "grover"          union
  select "eisenhower"       , "fillmore"        union
  select "delano"           , "jackson";

create table dogs as
  select "abraham" as name, "long" as fur, 26 as height union
  select "barack"         , "short"      , 52           union
  select "clinton"        , "long"       , 47           union
  select "delano"         , "long"       , 46           union
  select "eisenhower"     , "short"      , 35           union
  select "fillmore"       , "curly"      , 32           union
  select "grover"         , "short"      , 28           union
  select "herbert"        , "curly"      , 31           union
  select "jackson"        , "long"       , 43;

-- All triples of dogs with the same fur that have increasing heights

select "=== Question 1 ===";
select a.name||'|'|| b.name ||'|'|| c.name
 from dogs as a, dogs as b, dogs as c
 where a.fur = b.fur and b.fur=c.fur
and  a.name != b.name and a.name!=c.name
   and a.height < b.height and b.height < c.height;

-- Expected output:
--   abraham|delano|clinton
--   abraham|jackson|clinton
--   abraham|jackson|delano
--   grover|eisenhower|barack
--   jackson|delano|clinton

-- The sum of the heights of at least 3 dogs with the same fur, ordered by sum

select "=== Question 2 ===";
 select a.fur, a.height+b.height+c.height totheight
 from dogs as a, dogs as b, dogs as c
 where a.fur = b.fur and b.fur=c.fur
and  a.name != b.name and a.name!=c.name
   and a.height < b.height and b.height < c.height
order by a.height+b.height+c.height;

-- Expected output:
--   long|115
--   short|115
--   long|116
--   long|119
--   long|136
--   long|162

-- The terms of g(n) where g(n) = g(n-1) + 2*g(n-2) + 3*g(n-3) and g(n) = n if n <= 3

select "=== Question 3 ===";
WITH FibonacciNumbers (RecursionLevel, gnone,gntwo,gnthree,ans) 
AS ( 
  select 1,0,0,0,1 
  union all 
  select 2,1,0,0,2 
  union all 
  select 3,2,1,0,3
  union all
   -- Anchor member definition
   SELECT  4  AS RecursionLevel,
           3  AS gnone,
           2  AS gntwo,
        1 as gnthree,
         3+2*2+3*1 ans
   UNION ALL
   -- Recursive member definition
   SELECT  a.RecursionLevel + 1             AS RecursionLevel,
           a.ans                     AS gnone,
           a.gnone as gntwo,
        a.gntwo as gnthree,
    a.ans+2*a.gnone+3*a.gntwo ans
          
   FROM FibonacciNumbers a
   WHERE a.RecursionLevel < 20 and a.RecursionLevel > 3
)

SELECT  ans
FROM FibonacciNumbers fn;

-- Expected output:
--   1
--   2
--   3
--   10
--   22
--   51
--   125
--   293
--   696
--   1657
--   ...
--   9426875


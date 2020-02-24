#HOST=ec2-34-235-108-68.compute-1.amazonaws.com
HOST=ec2-34-235-108-68.compute-1.amazonaws.com
PORT=5432
DATABASE=d70uqvt93jbpoq
USER=ecxfhpxnovpejh
#export PGPASSWORD=0a0657538f39c41357b1de598e83f75940333ef42441c9eb41268f0a5d08dc2a
export PGPASSWORD=0a0657538f39c41357b1de598e83f75940333ef42441c9eb41268f0a5d08dc2a
psql -h $HOST -p $PORT -U $USER -d $DATABASE
#heroku pg:psql postgresql-contoured-15915 --app lilchengs
#list tables
#\d
#\d+
#\d tblData
#list databases
#\l
#quit
#\q
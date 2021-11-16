if docker logs chatty-client | grep -q ' andi~ test';
then
  echo "matched"
else
  echo "no match"
  exit 1
fi

#STDOUT=$(docker logs chatty-client);
#echo $STDOUT
#REG_EX='\[.*\] andi~ test'
#if [[ $STDOUT =~ $REG_EX ]]
#then
#  echo "matched"
#else
#  echo "no match"
#  exit 1
#fi
package graph_generators;

public class HashRingNode implements Comparable<HashRingNode>
{
	public enum Role
	{
		PUBLISHER,
		SUBSCRIBER,
		NONE
	}
	
	private double hashKey;
	private Role role;
	
	public HashRingNode(double hashKey)
	{
		this.role = Role.NONE;
		this.hashKey = hashKey;
	}

	public Role getRole()
	{
		return role;
	}

	public void setRole(Role role)
	{
		this.role = role;
	}

	public double getHashKey()
	{
		return hashKey;
	}
	
	@Override
	public boolean equals(Object o)
	{
		if (o == null || (o instanceof HashRingNode) == false)
		{
			return false;
		}
		
		if (((HashRingNode)o).hashKey != this.hashKey)
		{
			return false;
		}
		
		return true;
	}
	
	@Override
	public String toString()
	{
		return String.valueOf(hashKey);
	}

	@Override
	public int compareTo(HashRingNode node)
	{
		if (this.hashKey > node.hashKey)
		{
			return 1;
		}
		if (this.hashKey < node.hashKey)
		{
			return -1;
		}
		return 0;
	}
}

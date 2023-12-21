package redisson.remote;

public class RemoteImpl implements RemoteInterface {

	@Override
	public Long myMethod(final Long value) {
		return value * 2;
	}

}

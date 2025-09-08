/**
 * @see https://umijs.org/docs/max/access#access
 * */
export default function access(initialState: InitialState | undefined) {
  const { loginUser } = initialState ?? {};
  return {
    // canAdmin: currentUser && currentUser.access === 'admin',
    canAdmin: loginUser?.userRole === 'admin',
  };
}

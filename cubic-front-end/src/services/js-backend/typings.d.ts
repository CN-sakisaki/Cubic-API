declare namespace API {
  type BaseResponseboolean = {
    code?: number;
    data?: boolean;
    message?: string;
  };

  type BaseResponseImageVo = {
    code?: number;
    data?: ImageVo;
    message?: string;
  };

  type BaseResponseint = {
    code?: number;
    data?: number;
    message?: string;
  };

  type BaseResponseInterfaceInfo = {
    code?: number;
    data?: InterfaceInfo;
    message?: string;
  };

  type BaseResponseListInterfaceInfo = {
    code?: number;
    data?: InterfaceInfo[];
    message?: string;
  };

  type BaseResponseListInterfaceInfoVO = {
    code?: number;
    data?: InterfaceInfoVO[];
    message?: string;
  };

  type BaseResponseListUserInterfaceInfo = {
    code?: number;
    data?: UserInterfaceInfo[];
    message?: string;
  };

  type BaseResponseListUserVO = {
    code?: number;
    data?: UserVO[];
    message?: string;
  };

  type BaseResponselong = {
    code?: number;
    data?: number;
    message?: string;
  };

  type BaseResponseobject = {
    code?: number;
    data?: Record<string, any>;
    message?: string;
  };

  type BaseResponsePageInterfaceInfo = {
    code?: number;
    data?: PageInterfaceInfo;
    message?: string;
  };

  type BaseResponsePageUserInterfaceInfo = {
    code?: number;
    data?: PageUserInterfaceInfo;
    message?: string;
  };

  type BaseResponsePageUserVO = {
    code?: number;
    data?: PageUserVO;
    message?: string;
  };

  type BaseResponseUserInterfaceInfo = {
    code?: number;
    data?: UserInterfaceInfo;
    message?: string;
  };

  type BaseResponseUserVO = {
    code?: number;
    data?: UserVO;
    message?: string;
  };

  type DeleteRequest = {
    idList?: number[];
  };

  type Field = {
    fieldName?: string;
    value?: string;
  };

  type getCaptchaUsingPOSTParams = {
    emailAccount?: string;
  };

  type getInterfaceInfoByIdUsingGETParams = {
    /** id */
    id?: number;
  };

  type getUserByIdUsingGETParams = {
    /** id */
    id?: number;
  };

  type getUserInterfaceInfoByIdUsingGETParams = {
    /** id */
    id?: number;
  };

  type IdRequest = {
    id?: number;
  };

  type ImageVo = {
    name?: string;
    status?: string;
    uid?: string;
    url?: string;
  };

  type InterfaceInfo = {
    avatarUrl?: string;
    createTime?: string;
    description?: string;
    id?: number;
    isDeleted?: number;
    method?: string;
    name?: string;
    reduceScore?: number;
    requestExample?: string;
    requestHeader?: string;
    requestParams?: string;
    responseHeader?: string;
    responseParams?: string;
    returnFormat?: string;
    status?: number;
    totalInvokes?: number;
    updateTime?: string;
    url?: string;
    userId?: number;
  };

  type InterfaceInfoAddRequest = {
    description?: string;
    method?: string;
    name?: string;
    requestExample?: string;
    requestHeader?: string;
    requestParams?: RequestParamsField[];
    responseHeader?: string;
    responseParams?: ResponseParamsField[];
    returnFormat?: string;
    url?: string;
  };

  type InterfaceInfoInvokeRequest = {
    id?: number;
    requestParams?: Field[];
    userRequestParams?: string;
  };

  type InterfaceInfoUpdateRequest = {
    avatarUrl?: string;
    description?: string;
    id?: number;
    method?: string;
    name?: string;
    reduceScore?: number;
    requestExample?: string;
    requestHeader?: string;
    requestParams?: RequestParamsField[];
    responseHeader?: string;
    responseParams?: ResponseParamsField[];
    returnFormat?: string;
    status?: number;
    url?: string;
  };

  type InterfaceInfoVO = {
    avatarUrl?: string;
    createTime?: string;
    description?: string;
    id?: number;
    isDeleted?: number;
    method?: string;
    name?: string;
    reduceScore?: number;
    requestExample?: string;
    requestHeader?: string;
    requestParams?: string;
    responseHeader?: string;
    responseParams?: string;
    returnFormat?: string;
    status?: number;
    totalInvokes?: number;
    totalNum?: number;
    updateTime?: string;
    url?: string;
    userId?: number;
  };

  type listInterfaceInfoByPageUsingGETParams = {
    createTime?: string;
    current?: number;
    description?: string;
    id?: number;
    method?: string;
    name?: string;
    pageSize?: number;
    reduceScore?: number;
    requestHeader?: string;
    responseHeader?: string;
    returnFormat?: string;
    sortField?: string;
    sortOrder?: string;
    status?: number;
    url?: string;
    userId?: number;
  };

  type listInterfaceInfoUsingGETParams = {
    createTime?: string;
    current?: number;
    description?: string;
    id?: number;
    method?: string;
    name?: string;
    pageSize?: number;
    reduceScore?: number;
    requestHeader?: string;
    responseHeader?: string;
    returnFormat?: string;
    sortField?: string;
    sortOrder?: string;
    status?: number;
    url?: string;
    userId?: number;
  };

  type listUserByPageUsingGETParams = {
    createTime?: string;
    current?: number;
    email?: string;
    gender?: number;
    id?: number;
    pageSize?: number;
    sortField?: string;
    sortOrder?: string;
    updateTime?: string;
    userAccount?: string;
    userAvatar?: string;
    userName?: string;
    userRole?: string;
  };

  type listUserInterfaceInfoByPageUsingGETParams = {
    current?: number;
    id?: number;
    interfaceInfoId?: number;
    leftNum?: number;
    pageSize?: number;
    sortField?: string;
    sortOrder?: string;
    status?: number;
    totalNum?: number;
    userId?: number;
  };

  type listUserInterfaceInfoUsingGETParams = {
    current?: number;
    id?: number;
    interfaceInfoId?: number;
    leftNum?: number;
    pageSize?: number;
    sortField?: string;
    sortOrder?: string;
    status?: number;
    totalNum?: number;
    userId?: number;
  };

  type listUserUsingGETParams = {
    createTime?: string;
    current?: number;
    email?: string;
    gender?: number;
    id?: number;
    pageSize?: number;
    sortField?: string;
    sortOrder?: string;
    updateTime?: string;
    userAccount?: string;
    userAvatar?: string;
    userName?: string;
    userRole?: string;
  };

  type ModelAndView = {
    empty?: boolean;
    model?: Record<string, any>;
    modelMap?: Record<string, any>;
    reference?: boolean;
    status?:
      | 'ACCEPTED'
      | 'ALREADY_REPORTED'
      | 'BAD_GATEWAY'
      | 'BAD_REQUEST'
      | 'BANDWIDTH_LIMIT_EXCEEDED'
      | 'CHECKPOINT'
      | 'CONFLICT'
      | 'CONTINUE'
      | 'CREATED'
      | 'DESTINATION_LOCKED'
      | 'EXPECTATION_FAILED'
      | 'FAILED_DEPENDENCY'
      | 'FORBIDDEN'
      | 'FOUND'
      | 'GATEWAY_TIMEOUT'
      | 'GONE'
      | 'HTTP_VERSION_NOT_SUPPORTED'
      | 'IM_USED'
      | 'INSUFFICIENT_SPACE_ON_RESOURCE'
      | 'INSUFFICIENT_STORAGE'
      | 'INTERNAL_SERVER_ERROR'
      | 'I_AM_A_TEAPOT'
      | 'LENGTH_REQUIRED'
      | 'LOCKED'
      | 'LOOP_DETECTED'
      | 'METHOD_FAILURE'
      | 'METHOD_NOT_ALLOWED'
      | 'MOVED_PERMANENTLY'
      | 'MOVED_TEMPORARILY'
      | 'MULTIPLE_CHOICES'
      | 'MULTI_STATUS'
      | 'NETWORK_AUTHENTICATION_REQUIRED'
      | 'NON_AUTHORITATIVE_INFORMATION'
      | 'NOT_ACCEPTABLE'
      | 'NOT_EXTENDED'
      | 'NOT_FOUND'
      | 'NOT_IMPLEMENTED'
      | 'NOT_MODIFIED'
      | 'NO_CONTENT'
      | 'OK'
      | 'PARTIAL_CONTENT'
      | 'PAYLOAD_TOO_LARGE'
      | 'PAYMENT_REQUIRED'
      | 'PERMANENT_REDIRECT'
      | 'PRECONDITION_FAILED'
      | 'PRECONDITION_REQUIRED'
      | 'PROCESSING'
      | 'PROXY_AUTHENTICATION_REQUIRED'
      | 'REQUESTED_RANGE_NOT_SATISFIABLE'
      | 'REQUEST_ENTITY_TOO_LARGE'
      | 'REQUEST_HEADER_FIELDS_TOO_LARGE'
      | 'REQUEST_TIMEOUT'
      | 'REQUEST_URI_TOO_LONG'
      | 'RESET_CONTENT'
      | 'SEE_OTHER'
      | 'SERVICE_UNAVAILABLE'
      | 'SWITCHING_PROTOCOLS'
      | 'TEMPORARY_REDIRECT'
      | 'TOO_EARLY'
      | 'TOO_MANY_REQUESTS'
      | 'UNAUTHORIZED'
      | 'UNAVAILABLE_FOR_LEGAL_REASONS'
      | 'UNPROCESSABLE_ENTITY'
      | 'UNSUPPORTED_MEDIA_TYPE'
      | 'UPGRADE_REQUIRED'
      | 'URI_TOO_LONG'
      | 'USE_PROXY'
      | 'VARIANT_ALSO_NEGOTIATES';
    view?: View;
    viewName?: string;
  };

  type OrderItem = {
    asc?: boolean;
    column?: string;
  };

  type PageInterfaceInfo = {
    countId?: string;
    current?: number;
    maxLimit?: number;
    optimizeCountSql?: boolean;
    orders?: OrderItem[];
    pages?: number;
    records?: InterfaceInfo[];
    searchCount?: boolean;
    size?: number;
    total?: number;
  };

  type PageUserInterfaceInfo = {
    countId?: string;
    current?: number;
    maxLimit?: number;
    optimizeCountSql?: boolean;
    orders?: OrderItem[];
    pages?: number;
    records?: UserInterfaceInfo[];
    searchCount?: boolean;
    size?: number;
    total?: number;
  };

  type PageUserVO = {
    countId?: string;
    current?: number;
    maxLimit?: number;
    optimizeCountSql?: boolean;
    orders?: OrderItem[];
    pages?: number;
    records?: UserVO[];
    searchCount?: boolean;
    size?: number;
    total?: number;
  };

  type RequestParamsField = {
    desc?: string;
    fieldName?: string;
    id?: string;
    required?: string;
    type?: string;
  };

  type ResponseParamsField = {
    desc?: string;
    fieldName?: string;
    id?: string;
    type?: string;
  };

  type uploadFileUsingPOSTParams = {
    biz?: string;
  };

  type UserAddRequest = {
    email?: string;
    gender?: number;
    userAccount?: string;
    userAvatar?: string;
    userName?: string;
    userPassword?: string;
    userRole?: string;
  };

  type UserBindEmailRequest = {
    captcha?: string;
    emailAccount?: string;
  };

  type UserEmailLoginRequest = {
    captcha?: string;
    emailAccount?: string;
  };

  type UserInterfaceInfo = {
    createTime?: string;
    id?: number;
    interfaceInfoId?: number;
    isDeleted?: number;
    totalNum?: number;
    updateTime?: string;
    userId?: number;
  };

  type UserInterfaceInfoAddRequest = {
    interfaceInfoId?: number;
    leftNum?: number;
    totalNum?: number;
    userId?: number;
  };

  type UserInterfaceInfoUpdateRequest = {
    id?: number;
    leftNum?: number;
    status?: number;
    totalNum?: number;
  };

  type UserLoginRequest = {
    userAccount?: string;
    userPassword?: string;
  };

  type UserRegisterRequest = {
    checkPassword?: string;
    email?: string;
    userAccount?: string;
    userPassword?: string;
  };

  type UserUnBindEmailRequest = {
    captcha?: string;
    emailAccount?: string;
  };

  type UserUpdateRequest = {
    email?: string;
    gender?: number;
    id?: number;
    userAccount?: string;
    userAvatar?: string;
    userName?: string;
    userPassword?: string;
    userRole?: string;
  };

  type UserVO = {
    accessKey?: string;
    balance?: number;
    createTime?: string;
    email?: string;
    gender?: string;
    id?: number;
    invitationCode?: string;
    secretKey?: string;
    status?: number;
    userAccount?: string;
    userAvatar?: string;
    userName?: string;
    userRole?: string;
  };

  type View = {
    contentType?: string;
  };
}

// import {
//   getInterfaceInfoByIdUsingGet,
//   invokeInterfaceInfoUsingPost,
// } from '@/services/js-backend/interfaceInfoController';
// import { PageContainer } from '@ant-design/pro-components';
// import { Button, Card, Descriptions, Divider, Form, message } from 'antd';
// import TextArea from 'antd/es/input/TextArea';
// import React, { useEffect, useState } from 'react';
// import { useParams } from 'react-router';
//
// const Index: React.FC = () => {
//   const [loading, setLoading] = useState(false);
//   const [data, setData] = useState<API.InterfaceInfo>();
//   // 存储变量结果
//   const [invokeRes, setInvokeRes] = useState<any>();
//   // 调用加载状态变量，默认false
//   const [invokeLoading, setInvokeLoading] = useState(false);
//   // 使用 useParams 钩子函数获取动态路由参数
//   const params = useParams();
//   // 检查invokeRes是否存在以及是否为403错误
//   // const isForbiddenError =
//   //   typeof invokeRes === 'string' && invokeRes.includes('response status: 403');
//   // 定义一个日期格式化函数
//   const formatDate = (isoDateString) => {
//     const date = new Date(isoDateString);
//     const options = {
//       year: 'numeric',
//       month: 'long',
//       day: 'numeric',
//       hour: '2-digit',
//       minute: '2-digit',
//       second: '2-digit',
//       hour12: false,
//     };
//     return date.toLocaleString('zh-CN', options);
//   };
//
//   const loadData = async () => {
//     // 检查动态路由参数是否存在
//     if (!params.id) {
//       message.error('参数不存在');
//       return;
//     }
//     setLoading(true);
//     try {
//       // 发起请求获取接口信息，接受一个包含 id 参数的对象作为参数
//       const res = await getInterfaceInfoByIdUsingGet({
//         id: Number(params.id),
//       });
//       // 格式化时间
//       res.data.createTime = formatDate(res.data.createTime);
//       res.data.updateTime = formatDate(res.data.updateTime);
//       // 将获取到的接口信息设置到 data 状态中
//       setData(res.data);
//     } catch (error: any) {
//       message.error('请求失败', +error.message);
//     }
//     // 请求完成，设置 loading 状态为 false，表示请求结束，可以停止加载状态的显示
//     setLoading(false);
//   };
//
//   useEffect(() => {
//     // 页面加载完成后调用加载数据的函数
//     loadData();
//   }, []);
//
//   const onFinish = async (values: any) => {
//     if (!params.id) {
//       message.error('接口不存在');
//       return;
//     }
//     setInvokeLoading(true);
//     try {
//       const res = await invokeInterfaceInfoUsingPost({
//         id: params.id,
//         ...values,
//       });
//       setInvokeRes(res.data);
//       message.success('请求成功');
//     } catch (error: any) {
//       message.error('操作失败，' + error.message);
//     }
//     setInvokeLoading(false);
//   };
//
//   return (
//     <PageContainer title="在线接口开发平台">
//       <Card>
//         {data ? (
//           <Descriptions title={data.name} column={1}>
//             <Descriptions.Item label="接口状态">{data.status ? '开启' : '关闭'}</Descriptions.Item>
//             <Descriptions.Item label="描述">{data.description}</Descriptions.Item>
//             <Descriptions.Item label="请求地址">{data.url}</Descriptions.Item>
//             <Descriptions.Item label="请求方法">{data.method}</Descriptions.Item>
//             <Descriptions.Item label="请求参数">{data.requestParams}</Descriptions.Item>
//             <Descriptions.Item label="请求头">{data.requestHeader}</Descriptions.Item>
//             <Descriptions.Item label="响应头">{data.responseHeader}</Descriptions.Item>
//             <Descriptions.Item label="创建时间">{data.createTime}</Descriptions.Item>
//             <Descriptions.Item label="更新时间">{data.updateTime}</Descriptions.Item>
//           </Descriptions>
//         ) : (
//           // 将 data 对象转换为 JSON 字符串
//           // JSON.stringify(data)
//           <>接口不存在</>
//         )}
//       </Card>
//       <Divider />
//       <Card title="在线测试">
//         <Form name="invoke" layout="vertical" onFinish={onFinish}>
//           <Form.Item label="Param" name="userRequestParams">
//             <TextArea />
//           </Form.Item>
//           <Form.Item wrapperCol={{ span: 16 }}>
//             <Button type="primary" htmlType="submit">
//               发送
//             </Button>
//           </Form.Item>
//         </Form>
//       </Card>
//       <Divider />
//       <Card title="返回结果" loading={invokeLoading}>
//         <p>{invokeRes}</p>
//       </Card>
//     </PageContainer>
//   );
// };
//
// export default Index;

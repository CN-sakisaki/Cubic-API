import { listInterfaceInfoByPageUsingGet } from '@/services/js-backend/interfaceInfoController';
import { PageContainer } from '@ant-design/pro-components';
import { List, message } from 'antd';
import React, { useEffect, useState } from 'react';

const Index: React.FC = () => {
  // 使用 useState 和泛型来定义组件内的状态
  // 加载状态
  const [loading, setLoading] = useState(false);
  // 列表数据
  const [list, setList] = useState<API.InterfaceInfo[]>([]);
  // 总数
  const [total, setTotal] = useState<number>(0);

  // 定义异步加载数据的函数
  const loadData = async (current = 1, pageSize = 5) => {
    // 开始加载数据，设置 loading 状态为 true
    setLoading(true);
    try {
      // 调用接口获取数据
      const res = await listInterfaceInfoByPageUsingGet({
        current,
        pageSize,
      });
      // 将请求返回的数据设置到列表数据状态中
      setList(res?.data?.records ?? []);
      // 将请求返回的总数设置到总数状态中
      setTotal(res?.data?.total ?? 0);
      // 捕获请求失败的错误信息
    } catch (error: any) {
      // 请求失败时提示错误信息
      message.error('请求失败，' + error.message);
    }
    // 数据加载成功或失败后，设置 loading 状态为 false
    setLoading(false);
  };

  useEffect(() => {
    // 页面加载完成后调用加载数据的函数
    loadData();
  }, []);

  return (
    <PageContainer title="Cubic API 在线接口开发平台">
      <List
        className="my-list"
        loading={loading}
        itemLayout="horizontal"
        dataSource={list}
        renderItem={(item) => {
          const apiLink = `/interface_info/${item.id}`;
          return (
            <List.Item
              actions={[
                <a key={item.id} href={apiLink}>
                  查看接口
                </a>,
              ]}
            >
              <List.Item.Meta
                title={<a href={apiLink}>{item.name}</a>}
                description={item.description}
              />
            </List.Item>
          );
        }}
        // 分页配置
        pagination={{
          showTotal(total: number) {
            return '总数:' + total;
          },
          pageSize: 5,
          total,
          onChange(page, pageSize) {
            loadData(page, pageSize);
          },
        }}
      />
    </PageContainer>
  );
};

export default Index;

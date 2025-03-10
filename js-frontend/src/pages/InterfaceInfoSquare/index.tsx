import { listInterfaceInfoByPageUsingGet } from '@/services/js-backend/interfaceInfoController';
import ProCard from '@ant-design/pro-card';
import { history } from '@umijs/max';
import { Badge, Image, List, Spin } from 'antd';
import React, { useEffect, useState } from 'react';

const InterfaceSquare: React.FC = () => {
  const [data, setData] = useState<API.InterfaceInfo[]>([]);
  const [total, setTotal] = useState<number>();
  const [pageSize] = useState<number>(12);
  const [loading, setLoading] = useState<boolean>(false);

  const loadData = async (current = 1) => {
    setLoading(true);
    const res = await listInterfaceInfoByPageUsingGet({
      current: current,
      pageSize: pageSize,
      sortField: 'totalInvokes',
      sortOrder: 'descend',
    });
    if (res.code === 0 && res.data) {
      setData(res?.data?.records || []);
      setTotal(res.data.total);
      setLoading(false);
    } else {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadData();
  }, []);

  return (
    <>
      <br />
      <br />
      <Spin spinning={loading}>
        <List
          pagination={{
            onChange: (page) => {
              loadData(page);
            },
            pageSize: pageSize,
            total: total,
          }}
          grid={{
            gutter: 20,
            xs: 1,
            sm: 1,
            md: 2,
            lg: 4,
            xl: 5,
            xxl: 6,
          }}
          dataSource={data}
          renderItem={(item, index) => (
            <List.Item>
              <ProCard key={index} bordered hoverable direction="column" style={{ height: 270 }}>
                <ProCard
                  layout="center"
                  onClick={() => {
                    history.push(`/interface_info/${item.id}`);
                  }}
                >
                  <Badge count={item.totalInvokes} overflowCount={999999999} color="#eb4d4b">
                    <Image
                      style={{ width: 80, borderRadius: 8, marginLeft: 10 }}
                      src={item?.avatarUrl ?? '../icons/icon_mo_fang.png'}
                      alt={item.name}
                      preview={false}
                    />
                  </Badge>
                </ProCard>
                <ProCard
                  onClick={() => {
                    history.push(`/interface_info/${item.id}`);
                  }}
                  layout="center"
                  style={{ marginTop: -10, fontSize: 16 }}
                >
                  {item.name}
                </ProCard>
                <ProCard
                  onClick={() => {
                    history.push(`/interface_info/${item.id}`);
                  }}
                  layout="center"
                  style={{ marginTop: -18, fontSize: 14 }}
                >
                  {!item.description
                    ? '暂无接口描述'
                    : item.description.length > 15
                    ? item.description.slice(0, 15) + '...'
                    : item.description}
                </ProCard>
              </ProCard>
            </List.Item>
          )}
        />
      </Spin>
    </>
  );
};

export default InterfaceSquare;

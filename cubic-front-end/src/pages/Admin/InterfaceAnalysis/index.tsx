import {listTopInvokeInterfaceInfoUsingGet} from '@/services/js-backend/analysisController';
import {PageContainer} from '@ant-design/pro-components';
import '@umijs/max';
import {Spin} from 'antd';
import ReactECharts from 'echarts-for-react';
import React, {useEffect, useState} from 'react';

/**
 * 接口分析
 * @constructor
 */
const InterfaceAnalysis: React.FC = () => {
  const [data, setData] = useState<API.InterfaceInfoVO[]>([]);
  const [loading, setLoading] = useState<boolean>(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    const fetchData = async () => {
      try {
        const res = await listTopInvokeInterfaceInfoUsingGet();
        if (res.data) {
          setData(res.data);
        }
      } catch (e: any) {
        setError('数据获取失败');
      } finally {
        setLoading(false);
      }
    };
    fetchData();
  }, []);

  const chartData = data.map((item) => {
    return {
      value: item.totalNum,
      name: item.name,
    };
  });

  const option = {
    title: {
      text: '调用次数最多的接口TOP5',
      left: 'center',
    },
    tooltip: {
      trigger: 'item',
      formatter: (params: any) => {
        return `${params.name}: ${params.value}`; // 直接显示接口名称和调用次数
      },
    },
    legend: {
      orient: 'vertical',
      left: 'left',
    },
    series: [
      {
        name: '接口信息', // 修改此处名称
        type: 'pie',
        radius: '70%',
        data: chartData,
        emphasis: {
          itemStyle: {
            shadowBlur: 10,
            shadowOffsetX: 0,
            shadowColor: 'rgba(0, 0, 0, 0.5)',
          },
        },
      },
    ],
  };

  return (
    <PageContainer>
      {loading ? (
        <Spin tip="数据加载中..." />
      ) : error ? (
        <div style={{ color: 'red' }}>{error}</div>
      ) : (
        <>
          <ReactECharts option={option} />
        </>
      )}
    </PageContainer>
  );
};

export default InterfaceAnalysis;

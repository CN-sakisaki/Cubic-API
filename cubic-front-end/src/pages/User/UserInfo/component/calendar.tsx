import { ProCard } from '@ant-design/pro-components';
import { Button, Tooltip } from 'antd';
import type { EChartsOption } from 'echarts';
import * as echarts from 'echarts';
import React from 'react';

// 独立声明虚拟数据生成函数
const getVirtualData = (year: string): [string, number][] => {
  const date = +echarts.time.parse(`${year}-01-01`);
  const end = +echarts.time.parse(`${year}-12-31`);
  const dayTime = 3600 * 24 * 1000;
  const data: [string, number][] = [];

  for (let time = date; time <= end; time += dayTime) {
    data.push([
      echarts.time.format(time, '{yyyy}-{MM}-{dd}', false),
      Math.floor(Math.random() * 10000),
    ]);
  }
  return data;
};

// 定义标准的 ECharts 配置类型
const getChartOption = (): EChartsOption => ({
  visualMap: {
    show: false,
    min: 0,
    max: 10000,
  },
  calendar: {
    range: '2025',
    cellSize: 15,
    itemStyle: {
      borderWidth: 6,
      borderColor: '#ffffff',
      borderRadius: 2,
      color: '#ebedf0',
    },
    splitLine: {
      show: true,
      lineStyle: {
        color: '#ddd',
        width: 3,
        type: 'solid' as const, // 明确类型断言
      },
    },
    monthLabel: {
      show: true,
      nameMap: 'cn',
      position: 'start',
      color: '#666',
      fontSize: 12,
      margin: 8,
      align: 'left' as const, // 类型断言
    },
    yearLabel: { show: false },
    dayLabel: { show: false },
  },
  series: [
    {
      type: 'heatmap',
      coordinateSystem: 'calendar',
      data: getVirtualData('2025'),
      itemStyle: {
        borderRadius: 2,
      },
      emphasis: { disabled: true },
    },
  ],
});

const calendar: React.FC = () => {
  // 签到处理逻辑
  const handleDailyCheckIn = () => {
    // 签到实现
  };

  return (
    <ProCard
      bordered
      type="inner"
      extra={
        <Tooltip title="每日签到可获取积分">
          <Button
            type="primary"
            onClick={handleDailyCheckIn}
            style={{
              padding: '6px 12px',
              fontSize: '14px',
            }}
          >
            每日签到
          </Button>
        </Tooltip>
      }
    >
      {/* 这里放置 ECharts 图表容器 */}
      <div
        id="calendar-chart"
        style={{
          height: 300,
          margin: '16px -24px', // 对齐 ProCard 的内边距
        }}
      />
    </ProCard>
  );
};

export default calendar;

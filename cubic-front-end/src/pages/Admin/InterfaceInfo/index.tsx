import CreateModal from '@/pages/Admin/InterfaceInfo/components/CreateModal';
import UpdateModal from '@/pages/Admin/InterfaceInfo/components/UpdateModal';
import {
  addInterfaceInfoUsingPost,
  deleteInterfaceInfoUsingPost,
  listInterfaceInfoByPageUsingGet,
  offlineInterfaceInfoUsingPost,
  onlineInterfaceInfoUsingPost,
  updateInterfaceInfoUsingPost,
} from '@/services/js-backend/interfaceInfoController';
import { PlusOutlined } from '@ant-design/icons';
import type { ActionType, ProColumns, ProDescriptionsItemProps } from '@ant-design/pro-components';
import {
  FooterToolbar,
  PageContainer,
  ProDescriptions,
  ProTable,
} from '@ant-design/pro-components';
import '@umijs/max';
import { Button, Drawer, message } from 'antd';
import React, { useRef, useState } from 'react';

const TableList: React.FC = () => {
  /**
   * @en-US Pop-up window of new window
   * @zh-CN 新建窗口的弹窗
   *  */
  const [createModalOpen, handleModalOpen] = useState<boolean>(false);
  /**
   * @en-US The pop-up window of the distribution update window
   * @zh-CN 分布更新窗口的弹窗
   * */
  const [updateModalOpen, handleUpdateModalOpen] = useState<boolean>(false);
  const [showDetail, setShowDetail] = useState<boolean>(false);
  const actionRef = useRef<ActionType>();
  const [currentRow, setCurrentRow] = useState<API.InterfaceInfo>();
  const [selectedRowsState, setSelectedRows] = useState<API.InterfaceInfo[]>([]);
  // const [selectedRowKeys, setSelectedRowKeys] = useState<React.Key[]>([]);

  // 模态框的变量在TableList组件里，所以把增删改节点都放进来
  /**
   * @en-US Add node
   * @zh-CN 添加节点
   * @param fields
   */
  const handleAdd = async (fields: API.InterfaceInfoAddRequest) => {
    const hide = message.loading('正在添加');
    try {
      await addInterfaceInfoUsingPost({
        ...fields,
      });
      hide();
      // 如果调用成功会提示'创建成功'
      message.success('创建成功');
      // 创建成功就关闭这个模态框
      handleModalOpen(false);
      return true;
    } catch (error: any) {
      hide();
      // 否则提示'创建失败' + 报错信息
      message.error('创建失败,' + error.message);
      return false;
    }
  };

  /**
   * @en-US Update node
   * @zh-CN 更新节点
   * @param fields
   */
  const handleUpdate = async (fields: API.InterfaceInfoUpdateRequest) => {
    if (!currentRow) {
      return;
    }
    const hide = message.loading('修改中');
    try {
      await updateInterfaceInfoUsingPost({
        id: currentRow.id,
        ...fields,
      });
      hide();
      message.success('操作成功');
      return true;
    } catch (error: any) {
      hide();
      message.error('操作失败，' + error.message);
      return false;
    }
  };

  /**
   *  Delete node
   * @zh-CN 删除节点
   * @param record
   */
  const handleRemove = async (record: API.DeleteRequest[]) => {
    const hide = message.loading('正在删除');
    if (!record) return true;
    try {
      await deleteInterfaceInfoUsingPost({
        // 拿到idList就能删除数据
        idList: [record],
      });
      hide();
      message.success('删除成功');
      // 删除成功自动刷新表单
      actionRef.current?.reload();
      return true;
    } catch (error: any) {
      hide();
      message.error('删除失败,' + error.message);
      return false;
    }
  };

  /**
   *  public node
   * @zh-CN 发布节点
   * @param record
   */
  const handleOnline = async (record: API.IdRequest[]) => {
    const hide = message.loading('发布中');
    if (!record) return true;
    try {
      await onlineInterfaceInfoUsingPost({
        // 拿到id就能删除数据
        id: record,
      });
      hide();
      message.success('操作成功');
      // 删除成功自动刷新表单
      actionRef.current?.reload();
      return true;
    } catch (error: any) {
      hide();
      message.error('操作失败,' + error.message);
      return false;
    }
  };

  /**
   *  close node
   * @zh-CN 下线节点
   *
   * @param record
   */
  const handleOffline = async (record: API.IdRequest[]) => {
    const hide = message.loading('发布中');
    if (!record) return true;
    try {
      await offlineInterfaceInfoUsingPost({
        // 拿到id就能删除数据
        id: record,
      });
      hide();
      message.success('操作成功');
      // 删除成功自动刷新表单
      actionRef.current?.reload();
      return true;
    } catch (error: any) {
      hide();
      message.error('操作失败,' + error.message);
      return false;
    }
  };

  /**
   * @en-US International configuration
   * @zh-CN 国际化配置
   * */
  const columns: ProColumns<API.InterfaceInfo>[] = [
    {
      title: '序号',
      dataIndex: 'id',
      valueType: 'index',
    },
    {
      title: '接口名称',
      dataIndex: 'name',
      valueType: 'text',
      formItemProps: {
        rules: [
          {
            required: true,
          },
        ],
      },
    },
    {
      title: '描述',
      dataIndex: 'description',
      valueType: 'textarea',
    },
    {
      title: '请求方法',
      dataIndex: 'method',
      valueType: 'select',
      valueEnum: {
        // 定义下拉框的选项
        GET: { text: 'GET' },
        POST: { text: 'POST' },
        DELETE: { text: 'DELETE' },
        PUT: { text: 'PUT' },
      },
    },
    {
      title: 'url',
      dataIndex: 'url',
      valueType: 'text',
    },
    {
      title: '请求参数',
      dataIndex: 'requestParams',
      hideInSearch: true,
      valueType: 'jsonCode',
      hideInTable: true,
    },
    {
      title: '请求头',
      dataIndex: 'requestHeader',
      hideInSearch: true,
      valueType: 'jsonCode',
      hideInTable: true,
    },
    {
      title: '响应头',
      dataIndex: 'responseHeader',
      hideInSearch: true,
      valueType: 'jsonCode',
      hideInTable: true,
    },
    {
      title: '状态',
      dataIndex: 'status',
      hideInForm: true,
      valueEnum: {
        0: {
          text: '关闭',
          status: 'Default',
        },
        1: {
          text: '开启',
          status: 'Processing',
        },
      },
    },
    {
      title: '总调用次数',
      dataIndex: 'totalInvokes',
      valueType: 'text',
      hideInForm: true,
      hideInSearch: true,
      render: (totalInvokes) => `${totalInvokes}  次`,
    },
    {
      title: '调用所需金币数',
      dataIndex: 'reduceScore',
      valueType: 'text',
      render: (reduceScore) => `${reduceScore}  个`,
    },
    {
      title: '创建时间',
      dataIndex: 'createTime',
      valueType: 'dateTime',
      hideInSearch: true,
      hideInForm: true,
    },
    {
      title: '操作',
      dataIndex: 'option',
      valueType: 'option',
      render: (_, record) => [
        // 修改按钮
        <a
          key="config"
          onClick={() => {
            handleUpdateModalOpen(true);
            setCurrentRow(record);
          }}
        >
          修改
        </a>,
        // 发布按钮
        record.status === 0 ? (
          <a
            key="online"
            onClick={() => {
              handleOnline(record.id);
            }}
          >
            发布
          </a>
        ) : null,
        // 下线按钮
        record.status === 1 ? (
          <Button
            type="text"
            danger
            key="offline"
            onClick={() => {
              handleOffline(record.id);
            }}
          >
            下线
          </Button>
        ) : null,
        // 删除按钮
        <Button
          type="text"
          danger
          key="delete"
          onClick={() => {
            handleRemove(record.id);
          }}
        >
          删除
        </Button>,
      ],
    },
  ];
  return (
    <PageContainer>
      <ProTable<API.listInterfaceInfoByPageUsingGETParams>
        headerTitle={'查询表格'}
        actionRef={actionRef}
        rowKey={(record) => record.id.toString()} // 确保每行有唯一的键值
        search={{
          labelWidth: 120,
        }}
        toolBarRender={() => [
          <Button
            type="primary"
            key="primary"
            onClick={() => {
              handleModalOpen(true);
            }}
          >
            <PlusOutlined /> 新建
          </Button>,
        ]}
        request={async (params, sorter, filter) => {
          const pageSize = params.pageSize || 30; // 使用默认值或用户指定的页面大小
          const currentPage = params.current || 1; // 使用默认值或用户指定的当前页码
          const res = await listInterfaceInfoByPageUsingGet({
            ...params,
            pageSize: pageSize,
            current: currentPage, // 当前页码
          });
          // 如果后端请求给你返回了接口信息
          if (res?.data) {
            return {
              data: res?.data.records || [],
              success: true,
              total: res?.data.total,
            };
          } else {
            // 如果数据不存在，返回一个空数组，失败状态和零总数
            return {
              data: [],
              success: false,
              total: 0,
            };
          }
        }}
        columns={columns}
        rowSelection={{
          onChange: (_, selectedRows) => {
            console.log('Selected Rows:', selectedRows);
            setSelectedRows(selectedRows);
          },
        }}
      />
      {selectedRowsState?.length > 0 && (
        <FooterToolbar
          extra={
            <div>
              已选择{' '}
              <a
                style={{
                  fontWeight: 600,
                }}
              >
                {selectedRowsState.length}
              </a>{' '}
              项 &nbsp;&nbsp;
              <span>
                服务调用次数总计 {selectedRowsState.reduce((pre, item) => pre + item.callNo!, 0)} 万
              </span>
            </div>
          }
        >
          <Button
            onClick={async () => {
              await handleRemove(selectedRowsState);
              setSelectedRows([]);
              actionRef.current?.reloadAndRest?.();
            }}
          >
            批量删除
          </Button>
          <Button type="primary">批量审批</Button>
        </FooterToolbar>
      )}
      {/*创建一个CreateModal组件，用于在点击新增按钮时弹出*/}
      <CreateModal
        columns={columns}
        // 当取消按钮被点击时,设置更新模态框为false以隐藏模态窗口
        onCancel={() => handleModalOpen(false)}
        // 当用户点击提交按钮之后，调用handleAdd函数处理提交的数据，去请求后端添加数据(这里的报错不用管,可能里面组件的属性和外层的不一致)
        onSubmit={(values) => {
          handleAdd(values);
        }}
        // 根据更新窗口的值决定模态窗口是否显示
        visible={createModalOpen}
      ></CreateModal>
      {/*更新模态框*/}
      <UpdateModal
        // 不传递columns，模态框没有表单项
        columns={columns}
        onSubmit={async (value) => {
          const success = await handleUpdate(value);
          if (success) {
            handleUpdateModalOpen(false);
            setCurrentRow(undefined);
            if (actionRef.current) {
              actionRef.current.reload();
            }
          }
        }}
        onCancel={() => {
          handleUpdateModalOpen(false);
          if (!showDetail) {
            setCurrentRow(undefined);
          }
        }}
        visible={updateModalOpen}
        values={currentRow || {}}
      />

      <Drawer
        width={600}
        open={showDetail}
        onClose={() => {
          setCurrentRow(undefined);
          setShowDetail(false);
        }}
        closable={false}
      >
        {currentRow?.name && (
          <ProDescriptions<API.InterfaceInfo>
            column={2}
            title={currentRow?.name}
            request={async () => ({
              data: currentRow || {},
            })}
            params={{
              id: currentRow?.name,
            }}
            columns={columns as ProDescriptionsItemProps<API.InterfaceInfo>[]}
          />
        )}
      </Drawer>
    </PageContainer>
  );
};
export default TableList;
